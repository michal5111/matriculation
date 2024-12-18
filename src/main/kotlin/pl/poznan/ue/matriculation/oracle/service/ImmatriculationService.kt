package pl.poznan.ue.matriculation.oracle.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.annotation.LogExecutionTime
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.oracle.domain.*
import pl.poznan.ue.matriculation.oracle.repo.*
import pl.poznan.ue.matriculation.properties.ClauseAndRegulationPropertiesList
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters.lastDayOfMonth

@Service
class ImmatriculationService(
    private val studentService: StudentService,
    private val personProgrammeRepository: PersonProgrammeRepository,
    private val didacticCycleRepository: DidacticCycleRepository,
    private val erasmusService: ErasmusService,
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
    private val programmeRepository: ProgrammeRepository,
    private val programmeStageRepository: ProgrammeStageRepository,
    private val sourceOfFinancingRepository: SourceOfFinancingRepository,
    private val basisOfAdmissionRepository: BasisOfAdmissionRepository,
    private val clauseAndRegulationRepository: ClauseAndRegulationRepository,
    private val clauseAndRegulationProperties: ClauseAndRegulationPropertiesList
) {

    private val logger: Logger = LoggerFactory.getLogger(ImmatriculationService::class.java)

    @LogExecutionTime
    @Transactional(
        rollbackFor = [java.lang.Exception::class, java.lang.RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    fun immatriculate(
        person: Person,
        import: Import,
        application: Application
    ): Student? {
        val dataSourceId = application.dataSourceId ?: error("Datasource id is null")
        val applicationDtoDataSource = applicationDataSourceFactory.getDataSource(dataSourceId)
        val student = import.indexPoolCode?.let { studentService.createOrFindStudent(person, it) }
        val personProgramme = createPersonProgramme(
            person = person,
            import = import,
            student = student,
            certificate = application.certificate,
            sourceOfFinancing = application.sourceOfFinancing,
            basisOfAdmission = application.basisOfAdmission
        )
        student?.addPersonProgramme(personProgramme)
        val irkApplication = IrkApplication(
            applicationId = application.foreignId,
            confirmationStatus = 0,
            irkInstance = dataSourceId.let {
                applicationDtoDataSource.instanceUrl + '/'
            }
        )
        clauseAndRegulationProperties.regulations.forEach {
            if (it.programmePattern.toRegex().matches(import.programmeCode)) {
                addClauseAndRegulationConfirmation(
                    person = person,
                    personProgramme = personProgramme,
                    startDate = import.startDate,
                    it.code
                )
            }
        }
        logger.trace("Tworzę lub wybieram studenta")
        irkApplication.personProgramme = personProgramme
        personProgramme.irkApplication = irkApplication
        addPersonArrival(person, application, import.didacticCycleCode, personProgramme, import.startDate)
        personProgrammeRepository.save(personProgramme)
        irkApplication.confirmationStatus = try {
            val result = applicationDtoDataSource.postMatriculation(application.foreignId)
            logger.debug("postmatriculation result = {}", result)
            result
        } catch (e: Exception) {
            logger.error("Error in post matriculation method", e)
            0
        }
        return student
    }

    private fun addPersonArrival(
        person: Person,
        application: Application,
        didacticCycleCode: String,
        personProgramme: PersonProgramme,
        startDate: LocalDate
    ) = application.applicant?.erasmusData?.let {
        val didacticCycle = didacticCycleRepository.findByIdOrNull(didacticCycleCode)
            ?: error("Nie można znaleźć cyklu dydaktycznego")
        val didacticCycleYear = didacticCycleRepository.findDidacticCycleYearBySemesterDates(
            didacticCycle.dateFrom,
            didacticCycle.endDate
        ) ?: error("Nie można znaleźć cyklu dydaktycznego")
        val arrival = erasmusService.createArrival(
            erasmusData = it,
            didacticCycle = didacticCycle,
            didacticCycleYear = didacticCycleYear,
            didacticCycleCode = didacticCycleCode,
            startDate = startDate
        )
        personProgramme.addArrival(arrival)
        person.addPersonArrivals(arrival)
        arrival
    }

    private fun addClauseAndRegulationConfirmation(
        person: Person,
        personProgramme: PersonProgramme,
        startDate: LocalDate,
        regulationCode: String
    ) {
        val clauseAndRegulation = clauseAndRegulationRepository.findLatestByCode(regulationCode)
            ?: error("Unable to find clause and regulation with id: $regulationCode")
        val decisionDeadline = startDate.with(lastDayOfMonth())
        val clauseAndRegulationConfirmation = ClauseAndRegulationConfirmation(
            person = person,
            personProgramme = personProgramme,
            decisionDeadline = decisionDeadline,
            terminationDate = clauseAndRegulation.endOfApplication,
            clauseAndRegulation = clauseAndRegulation
        )
        clauseAndRegulation.clauseAndRegulationConfirmations.add(clauseAndRegulationConfirmation)
        personProgramme.clauseAndRegulationConfirmations.add(clauseAndRegulationConfirmation)
    }

    private fun createPersonProgramme(
        person: Person,
        import: Import,
        student: Student?,
        certificate: Document?,
        sourceOfFinancing: String?,
        basisOfAdmission: String?
    ): PersonProgramme {
        val personId = person.id ?: throw IllegalStateException("Person id is null")
        val programme = programmeRepository.getReferenceById(import.programmeCode)
        val didacticCycle = didacticCycleRepository.findByIdOrNull(import.didacticCycleCode)
            ?: error("Nie można znaleźć cyklu dydaktycznego")
        val entitlementDocument = getEntitlementDocument(certificate, person)
        val isDefaultProgramme = if (person.personProgrammes.any { it.isDefault == true }) {
            personProgrammeRepository.updateToNotDefault(personId, import.dateOfAddmision) == 1
        } else true
        val personProgramme = PersonProgramme(
            person = person,
            programme = programme,
            student = student,
            startDate = import.startDate,
            dateOfAddmision = import.dateOfAddmision,
            dateToNextPass = didacticCycle.dateTo,
            isDefault = isDefaultProgramme
        )
        entitlementDocument?.addPersonProgramme(personProgramme)
        addSourceOfFinancing(sourceOfFinancing, personProgramme, import.dateOfAddmision)
        addBasisOfAdmission(basisOfAdmission, personProgramme, import.dateOfAddmision)
        addPersonStage(personProgramme, didacticCycle, programme, import.stageCode)
        person.addPersonProgramme(personProgramme)
        return personProgramme
    }

    private fun getEntitlementDocument(
        certificate: Document?,
        person: Person
    ) = certificate?.let {
        person.entitlementDocuments.find {
            it.number == certificate.documentNumber
                && it.type == certificate.certificateUsosCode
        }
    }

    private fun addPersonStage(
        personProgramme: PersonProgramme,
        didacticCycle: DidacticCycle,
        programme: Programme,
        stageCode: String
    ) = personProgramme.addPersonStage(
        PersonStage(
            didacticCycle = didacticCycle,
            passStatus = 'X',
            personProgramme = personProgramme,
            programmeStage = programmeStageRepository.getReferenceById(
                ProgrammeStageId(programmeId = programme.code, stageId = stageCode)
            ),
            didacticCycleRequirement = didacticCycle,
            order = 1,
            endDate = didacticCycle.endDate,
        ).apply {
            conditionCount = programmeStage.conditionCount
        }
    )

    private fun addBasisOfAdmission(
        basisOfAdmission: String?,
        personProgramme: PersonProgramme,
        dateOfAddmision: LocalDate
    ) = basisOfAdmission?.let {
        personProgramme.addPersonProgrammeBasisOfAdmission(
            PersonProgrammeBasisOfAdmission(
                basisOfAdmission = basisOfAdmissionRepository.getReferenceById(it),
                dateFrom = dateOfAddmision,
                personProgramme = personProgramme
            )
        )
    }

    private fun addSourceOfFinancing(
        sourceOfFinancing: String?,
        personProgramme: PersonProgramme,
        dateOfAddmision: LocalDate
    ) = sourceOfFinancing?.let {
        personProgramme.addPersonProgrammeSourceOfFinancing(
            PersonProgrammeSourceOfFinancing(
                sourceOfFinancing = sourceOfFinancingRepository.getReferenceById(it),
                dateFrom = dateOfAddmision,
                personProgramme = personProgramme
            )
        )
    }
}
