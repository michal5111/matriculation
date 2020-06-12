package pl.poznan.ue.matriculation.oracle.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
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
        private val personProgrammeRepository: PersonProgrammeRepository,
        private val studentRepository: StudentRepository,
        private val entitlementDocumentRepository: EntitlementDocumentRepository
) {

    @Autowired
    @Qualifier("oracleDataSource")
    private lateinit var dataSource: DataSource

    private lateinit var jdbcCall: SimpleJdbcCall

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
        val indexType = indexTypeRepository.getOne(indexPoolCode)
        val foundStudent = studentRepository.findByPersonAndIndexType(person, indexType)
        if (foundStudent != null) {
            return foundStudent
        }
        val paramMap = MapSqlParameterSource()
                .addValue("p_typ", indexPoolCode)
        val resultMap: MutableMap<String, Any> = jdbcCall.execute(paramMap)
        val student = Student(
                indexType = indexType,
                organizationalUnit = organizationalUnitRepository.getOne(resultMap["p_jed_org_kod"] as String),
                indexNumber = resultMap["p_numer"] as String,
                mainIndex = 'T',
                person = person
        )
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
            certificate: Document?
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
                isDefault = if (getPreviousStudyEndDate(person, dateOfAddmision) <= dateOfAddmision) 'T' else 'N',
                entitlementDocument = certificate?.let {
                    entitlementDocumentRepository.getByPersonAndTypeAndNumber(
                            person,
                            it.certificateUsosCode!!,
                            it.documentNumber!!
                    )
                }
        )
        personProgramme.personStages.add(
                PersonStage(
                        didacticCycle = didacticCycle,
                        passStatus = 'X',
                        personProgramme = personProgramme,
                        programmeStage = programmeStageRepository.getOne(ProgrammeStageId(
                                programmeId = programme.code,
                                stageId = stageCode
                        )),
                        didacticCycleRequirement = didacticCycle,
                        order = 1,
                        endDate = didacticCycle.endDate
                )
        )
        person.personProgrammes.add(personProgramme)
        student.personProgrammes.add(personProgramme)
        return personProgramme
    }

    fun getPreviousStudyEndDate(person: Person, dateOfAddmision: Date): Date {
        return personProgrammeRepository.getPreviousStudyEndDate(person) ?: return dateOfAddmision
    }
}