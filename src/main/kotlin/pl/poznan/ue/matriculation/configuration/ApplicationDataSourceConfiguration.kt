package pl.poznan.ue.matriculation.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.poznan.ue.matriculation.applicantDataSources.*
import pl.poznan.ue.matriculation.dreamApply.dto.applicant.DreamApplyApplicantDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.DreamApplyApplicationDto
import pl.poznan.ue.matriculation.dreamApply.mapper.DreamApplyApplicantMapper
import pl.poznan.ue.matriculation.dreamApply.mapper.DreamApplyApplicationMapper
import pl.poznan.ue.matriculation.dreamApply.mapper.IncomingApplicantMapper
import pl.poznan.ue.matriculation.dreamApply.service.DreamApplyService
import pl.poznan.ue.matriculation.excelfile.dto.ExcelFileApplicantDto
import pl.poznan.ue.matriculation.excelfile.dto.ExcelFileApplicationDto
import pl.poznan.ue.matriculation.excelfile.mapper.ExcelFileApplicantMapper
import pl.poznan.ue.matriculation.excelfile.mapper.ExcelFileApplicationMapper
import pl.poznan.ue.matriculation.irk.dto.applicants.IrkApplicantDto
import pl.poznan.ue.matriculation.irk.dto.applications.IrkApplicationDTO
import pl.poznan.ue.matriculation.irk.mapper.IrkApplicantMapper
import pl.poznan.ue.matriculation.irk.mapper.IrkApplicationMapper
import pl.poznan.ue.matriculation.irk.service.IrkService
import pl.poznan.ue.matriculation.oracle.repo.ProgrammeRepository
import pl.poznan.ue.matriculation.oracle.repo.SchoolRepository

@Configuration
class ApplicationDataSourceConfiguration {

    @Value("\${pl.poznan.ue.matriculation.testIrkInstance}")
    private lateinit var testServiceUrl: String

    @Value("\${pl.poznan.ue.matriculation.testIrkInstanceKey}")
    private lateinit var testApiKey: String

    @Value("\${pl.poznan.ue.matriculation.testIrkInstanceSetAsAccepted}")
    private var testSetAsAccepted: Boolean = false

    @Value("\${pl.poznan.ue.matriculation.primaryIrkInstance}")
    private lateinit var primaryServiceUrl: String

    @Value("\${pl.poznan.ue.matriculation.primaryIrkInstanceKey}")
    private lateinit var primaryApiKey: String

    @Value("\${pl.poznan.ue.matriculation.primaryIrkInstanceSetAsAccepted}")
    private var primarySetAsAccepted: Boolean = false

    @Value("\${pl.poznan.ue.matriculation.dreamApplyInstance}")
    private lateinit var dreamApplyInstanceUrl: String

    @Value("\${pl.poznan.ue.matriculation.dreamApplyApiKey}")
    private lateinit var dreamApplyApiKey: String

    @Value("\${pl.poznan.ue.matriculation.incomingInstance}")
    private lateinit var incomingInstanceUrl: String

    @Value("\${pl.poznan.ue.matriculation.incomingApiKey}")
    private lateinit var incomingApiKey: String

    @Bean(name = ["IncomingService"])
    fun incomingService(): DreamApplyService {
        return DreamApplyService(
            apiKey = incomingApiKey,
            instanceUrl = incomingInstanceUrl
        )
    }

    @Bean(name = ["IncomingApplicantDataSource"])
    fun incomingApplicantDataSource(
        @Autowired @Qualifier("IncomingService") IncomingService: DreamApplyService,
        @Autowired schoolRepository: SchoolRepository
    ): IApplicationDataSource<DreamApplyApplicationDto, DreamApplyApplicantDto> {
        return IncomingDataSourceImpl(
            dreamApplyService = IncomingService,
            name = "Incoming",
            id = "INCOMING",
            applicantMapper = IncomingApplicantMapper(schoolRepository),
            applicationMapper = DreamApplyApplicationMapper(),
            status = "Accepted"
        )
    }

    @Bean(name = ["DreamApplyService"])
    fun dreamApplyService(): DreamApplyService {
        return DreamApplyService(
            apiKey = dreamApplyApiKey,
            instanceUrl = dreamApplyInstanceUrl
        )
    }

    @Bean(name = ["DreamApplyApplicantDataSource"])
    fun dreamApplyApplicantDataSource(
        @Autowired @Qualifier("DreamApplyService") dreamApplyService: DreamApplyService,
        @Autowired schoolRepository: SchoolRepository
    ): IApplicationDataSource<DreamApplyApplicationDto, DreamApplyApplicantDto> {
        return DreamApplyDataSourceImpl(
            dreamApplyService = dreamApplyService,
            name = "Dream Apply",
            id = "DREAM_APPLY",
            applicantMapper = DreamApplyApplicantMapper(schoolRepository),
            applicationMapper = DreamApplyApplicationMapper(),
            status = "Everything OK"
        )
    }

    @Bean(name = ["TestIrkService"])
    fun testIrkService(): IrkService {
        return IrkService(
            testServiceUrl,
            testApiKey
        )
    }

    @Bean(name = ["testIrkApplicantDataSource"])
    fun testIrkApplicantDataSource(
        @Autowired @Qualifier("TestIrkService") irkService: IrkService
    ): IApplicationDataSource<IrkApplicationDTO, IrkApplicantDto> {
        return IrkApplicationDataSourceImpl(
            id = "IRK_TEST",
            name = "Testowa IRK",
            irkService = irkService,
            setAsAccepted = testSetAsAccepted,
            irkApplicantMapper = IrkApplicantMapper(),
            irkApplicationMapper = IrkApplicationMapper()
        )
    }

    @Bean(name = ["primaryIrkService"])
    fun primaryIrkService(): IrkService {
        return IrkService(
            primaryServiceUrl,
            primaryApiKey
        )
    }

    @Bean(name = ["primaryIrkApplicantDataSource"])
    fun primaryIrkApplicantDataSource(
        @Autowired @Qualifier("primaryIrkService") irkService: IrkService
    ): IApplicationDataSource<IrkApplicationDTO, IrkApplicantDto> {
        return IrkApplicationDataSourceImpl(
            id = "IRK_PRIMARY",
            name = "Główna IRK",
            irkService = irkService,
            setAsAccepted = primarySetAsAccepted,
            irkApplicationMapper = IrkApplicationMapper(),
            irkApplicantMapper = IrkApplicantMapper()
        )
    }

    @Bean(name = ["excelFileApplicantDAtaSource"])
    fun excelFileApplicantDataSource(
        programmeRepository: ProgrammeRepository
    ): IApplicationDataSource<ExcelFileApplicationDto, ExcelFileApplicantDto> {
        return ExcelFileDataSourceImpl(
            excelFileApplicantMapper = ExcelFileApplicantMapper(),
            excelFileApplicationMapper = ExcelFileApplicationMapper(),
            programmeRepository = programmeRepository
        )
    }
}