package pl.poznan.ue.matriculation.oracle.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.configuration.LogExecutionTime
import pl.poznan.ue.matriculation.kotlinExtensions.getById
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.dto.ImportDtoJpa
import pl.poznan.ue.matriculation.oracle.domain.*
import pl.poznan.ue.matriculation.oracle.repo.*
import java.util.*


@Service
class StudentService(
    private val indexTypeRepository: IndexTypeRepository,
    private val organizationalUnitRepository: OrganizationalUnitRepository,
    private val programmeRepository: ProgrammeRepository,
    private val didacticCycleRepository: DidacticCycleRepository,
    private val programmeStageRepository: ProgrammeStageRepository,
    private val studentRepository: StudentRepository,
    private val entitlementDocumentRepository: EntitlementDocumentRepository,
    private val sourceOfFinancingRepository: SourceOfFinancingRepository,
    private val basisOfAdmissionRepository: BasisOfAdmissionRepository
) {
    val logger: Logger = LoggerFactory.getLogger(StudentService::class.java)

    @Value("\${pl.poznan.ue.matriculation.defaultStudentOrganizationalUnit}")
    lateinit var defaultStudentOrganizationalUnitCode: String

    @LogExecutionTime
    fun createOrFindStudent(person: Person, indexPoolCode: String): Student {
        val foundStudent =
            studentRepository.findByPersonIdAndIndexTypeCodeOrderByIndexNumberAsc(person.id, indexPoolCode)
        if (foundStudent.isNotEmpty()) {
            return foundStudent.last()
        }
        val indexNumberDto = studentRepository.getNewIndexNumber(indexPoolCode)
        val student = Student(
            indexType = indexTypeRepository.getById(indexPoolCode),
            organizationalUnit =
            if (indexNumberDto.organizationalUnitCode != null) organizationalUnitRepository.getById(indexNumberDto.organizationalUnitCode)
            else organizationalUnitRepository.getById(defaultStudentOrganizationalUnitCode),
            indexNumber = indexNumberDto.number,
            mainIndex = if (indexNumberDto.organizationalUnitCode == defaultStudentOrganizationalUnitCode) 'T'
            else 'N',
            person = person
        )
        if (student.organizationalUnit.code == defaultStudentOrganizationalUnitCode) {
            studentRepository.findByPersonIdAndMainIndex(person.id, 'T')?.apply {
                mainIndex = 'N'
            }?.let {
                studentRepository.save(it)
            }
        }
        person.student.add(student)
        return studentRepository.save(student)
    }

    fun createPersonProgramme(
        person: Person,
        importDto: ImportDtoJpa,
        student: Student,
        certificate: Document?,
        sourceOfFinancing: String?,
        basisOfAdmission: String?,
        isDefault: Boolean
    ): PersonProgramme {
        val programme = programmeRepository.getById(importDto.programmeCode)
        val didacticCycle = didacticCycleRepository.getById(importDto.didacticCycleCode)
        val personProgramme = PersonProgramme(
            person = person,
            programme = programme,
            student = student,
            startDate = importDto.startDate,
            dateOfAddmision = importDto.dateOfAddmision,
            dateToNextPass = didacticCycle.dateTo,
            isDefault = if (isDefault) 'T' else 'N',
            entitlementDocument = getCertificate(certificate, person)
        )
        addSourceOfFinancing(sourceOfFinancing, personProgramme, importDto.dateOfAddmision)
        addBasisOfAdmission(basisOfAdmission, personProgramme, importDto.dateOfAddmision)
        addPersonStage(personProgramme, didacticCycle, programme, importDto.stageCode)
        person.personProgrammes.add(personProgramme)
        student.personProgrammes.add(personProgramme)
        return personProgramme
    }

    private fun getCertificate(
        certificate: Document?,
        person: Person
    ) = certificate?.let {
        entitlementDocumentRepository.getByPersonIdAndTypeAndNumber(
            person.id,
            it.certificateUsosCode!!,
            it.documentNumber
        )
    }


    private fun addPersonStage(
        personProgramme: PersonProgramme,
        didacticCycle: DidacticCycle,
        programme: Programme,
        stageCode: String
    ) = personProgramme.personStages.add(
        PersonStage(
            didacticCycle = didacticCycle,
            passStatus = 'X',
            personProgramme = personProgramme,
            programmeStage = programmeStageRepository.getById(
                ProgrammeStageId(
                    programmeId = programme.code,
                    stageId = stageCode
                )
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
        dateOfAddmision: Date
    ) = basisOfAdmission?.let {
        personProgramme.personProgrammeBasisOfAdmission.add(
            PersonProgrammeBasisOfAdmission(
                basisOfAdmission = basisOfAdmissionRepository.getById(it),
                dateFrom = dateOfAddmision,
                personProgramme = personProgramme
            )
        )
    }

    private fun addSourceOfFinancing(
        sourceOfFinancing: String?,
        personProgramme: PersonProgramme,
        dateOfAddmision: Date
    ) = sourceOfFinancing?.let {
        personProgramme.personProgrammeSourceOfFinancing.add(
            PersonProgrammeSourceOfFinancing(
                sourceOfFinancing = sourceOfFinancingRepository.getById(it),
                dateFrom = dateOfAddmision,
                personProgramme = personProgramme
            )
        )
    }
}