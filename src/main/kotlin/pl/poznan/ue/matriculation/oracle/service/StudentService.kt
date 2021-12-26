package pl.poznan.ue.matriculation.oracle.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.configuration.LogExecutionTime
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.import.Import
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
    private val sourceOfFinancingRepository: SourceOfFinancingRepository,
    private val basisOfAdmissionRepository: BasisOfAdmissionRepository
) {
    val logger: Logger = LoggerFactory.getLogger(StudentService::class.java)

    @Value("\${pl.poznan.ue.matriculation.defaultStudentOrganizationalUnit}")
    lateinit var defaultStudentOrganizationalUnitCode: String

    @LogExecutionTime
    fun createOrFindStudent(person: Person, indexPoolCode: String): Student {
        person.students.findLast {
            it.indexType.code == indexPoolCode
        }?.let {
            return it
        }
        val indexNumberDto = studentRepository.getNewIndexNumber(indexPoolCode)
        val organizationalUnit =
            if (indexNumberDto.organizationalUnitCode != null) organizationalUnitRepository.getById(indexNumberDto.organizationalUnitCode)
            else organizationalUnitRepository.getById(defaultStudentOrganizationalUnitCode)
        if (organizationalUnit.code == defaultStudentOrganizationalUnitCode) {
            person.students.find {
                it.mainIndex
            }?.let {
                studentRepository.setMainIndex(it.id ?: -1, false)
            }
        }
        val student = Student(
            indexType = indexTypeRepository.getById(indexPoolCode),
            organizationalUnit = organizationalUnit,
            indexNumber = indexNumberDto.number,
            mainIndex = indexNumberDto.organizationalUnitCode == defaultStudentOrganizationalUnitCode,
            person = person
        )
        person.addStudent(student)
        return studentRepository.save(student)
    }

    fun createPersonProgramme(
        person: Person,
        importDto: Import,
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
            isDefault = isDefault,
            entitlementDocument = getCertificate(certificate, person)
        )
        addSourceOfFinancing(sourceOfFinancing, personProgramme, importDto.dateOfAddmision)
        addBasisOfAdmission(basisOfAdmission, personProgramme, importDto.dateOfAddmision)
        addPersonStage(personProgramme, didacticCycle, programme, importDto.stageCode)
        person.addPersonProgramme(personProgramme)
        return personProgramme
    }

    private fun getCertificate(
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
        personProgramme.addPersonProgrammeBasisOfAdmission(
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
        personProgramme.addPersonProgrammeSourceOfFinancing(
            PersonProgrammeSourceOfFinancing(
                sourceOfFinancing = sourceOfFinancingRepository.getById(it),
                dateFrom = dateOfAddmision,
                personProgramme = personProgramme
            )
        )
    }
}
