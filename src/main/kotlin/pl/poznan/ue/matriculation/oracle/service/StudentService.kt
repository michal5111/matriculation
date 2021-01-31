package pl.poznan.ue.matriculation.oracle.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.SqlOutParameter
import org.springframework.jdbc.core.SqlParameter
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.oracle.domain.*
import pl.poznan.ue.matriculation.oracle.repo.*
import java.sql.Types
import java.util.*
import javax.annotation.PostConstruct
import javax.sql.DataSource


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
    private val basisOfAdmissionRepository: BasisOfAdmissionRepository,
    @Qualifier("oracleDataSource") private val dataSource: DataSource
) {

    private lateinit var jdbcCall: SimpleJdbcCall

    @Value("\${pl.poznan.ue.matriculation.defaultStudentOrganizationalUnit}")
    lateinit var defaultStudentOrganizationalUnitCode: String

    @PostConstruct
    fun init() {
        val jdbcTemplate = JdbcTemplate(dataSource)
        jdbcCall = SimpleJdbcCall(jdbcTemplate)
            .withSchemaName("USOS_PROD_TAB")
            .withCatalogName("pkg_immatrykulacja")
            .withProcedureName("nowy_indeks")
            .declareParameters(
                SqlParameter("p_typ", Types.VARCHAR),
                SqlOutParameter("p_numer", Types.VARCHAR),
                SqlOutParameter("p_jed_org_kod", Types.VARCHAR)
            )
    }

    fun createOrFindStudent(person: Person, indexPoolCode: String): Student {
        val foundStudent = studentRepository.findByPersonAndIndexTypeCodeOrderByIndexNumberAsc(person, indexPoolCode)
        if (foundStudent.isNotEmpty()) {
            return foundStudent.last()
        }
        val paramMap = MapSqlParameterSource()
            .addValue("p_typ", indexPoolCode)
        val resultMap: MutableMap<String, Any> = jdbcCall.execute(paramMap)
        val student = Student(
            indexType = indexTypeRepository.getOne(indexPoolCode),
            organizationalUnit =
            if (resultMap["p_jed_org_kod"] != null) organizationalUnitRepository.getOne(resultMap["p_jed_org_kod"] as String)
            else organizationalUnitRepository.getOne(defaultStudentOrganizationalUnitCode),
            indexNumber = resultMap["p_numer"] as String,
            mainIndex = if (resultMap["p_jed_org_kod"] == defaultStudentOrganizationalUnitCode) 'T'
            else 'N',
            person = person
        )
        if (student.organizationalUnit.code == defaultStudentOrganizationalUnitCode) {
            studentRepository.findByPersonAndMainIndex(person, 'T')?.apply {
                mainIndex = 'N'
            }?.let {
                studentRepository.save(it)
            }
        }
        person.student.add(student)
        return student
    }

    fun createPersonProgramme(
        person: Person,
        programmeCode: String,
        startDate: Date,
        dateOfAddmision: Date,
        stageCode: String,
        didacticCycleCode: String,
        student: Student,
        certificate: Document?,
        sourceOfFinancing: String?,
        basisOfAdmission: String?,
        isDefault: Boolean
    ): PersonProgramme {
        val programme = programmeRepository.getOne(programmeCode)
        val didacticCycle = didacticCycleRepository.getOne(didacticCycleCode)
        val personProgramme = PersonProgramme(
            person = person,
            programme = programme,
            student = student,
            startDate = startDate,
            dateOfAddmision = dateOfAddmision,
            dateToNextPass = didacticCycle.dateTo,
            isDefault = if (isDefault) 'T' else 'N',
            entitlementDocument = certificate?.let {
                entitlementDocumentRepository.getByPersonAndTypeAndNumber(
                    person,
                    it.certificateUsosCode!!,
                    it.documentNumber
                )
            }
        )
        sourceOfFinancing?.let {
            personProgramme.personProgrammeSourceOfFinancing.add(
                PersonProgrammeSourceOfFinancing(
                    sourceOfFinancing = sourceOfFinancingRepository.getOne(it),
                    dateFrom = dateOfAddmision,
                    personProgramme = personProgramme
                )
            )
        }
        basisOfAdmission?.let {
            personProgramme.personProgrammeBasisOfAdmission.add(
                PersonProgrammeBasisOfAdmission(
                    basisOfAdmission = basisOfAdmissionRepository.getOne(it),
                    dateFrom = dateOfAddmision,
                    personProgramme = personProgramme
                )
            )
        }
        personProgramme.personStages.add(
            PersonStage(
                didacticCycle = didacticCycle,
                passStatus = 'X',
                personProgramme = personProgramme,
                programmeStage = programmeStageRepository.getOne(
                    ProgrammeStageId(
                        programmeId = programme.code,
                        stageId = stageCode
                    )
                ),
                didacticCycleRequirement = didacticCycle,
                order = 1,
                endDate = didacticCycle.endDate,
                conditionCount = 2
            )
        )
        person.personProgrammes.add(personProgramme)
        student.personProgrammes.add(personProgramme)
        return personProgramme
    }
}