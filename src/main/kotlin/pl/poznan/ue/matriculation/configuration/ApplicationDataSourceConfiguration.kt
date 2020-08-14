package pl.poznan.ue.matriculation.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.poznan.ue.matriculation.applicantDataSources.DreamApplyDataSourceImpl
import pl.poznan.ue.matriculation.applicantDataSources.IApplicationDataSource
import pl.poznan.ue.matriculation.applicantDataSources.IrkApplicationDataSourceImpl
import pl.poznan.ue.matriculation.dreamApply.dto.applicant.DreamApplyApplicantDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.DreamApplyApplicationDto
import pl.poznan.ue.matriculation.dreamApply.mapper.DreamApplyApplicantMapper
import pl.poznan.ue.matriculation.dreamApply.mapper.DreamApplyApplicationMapper
import pl.poznan.ue.matriculation.dreamApply.service.DreamApplyService
import pl.poznan.ue.matriculation.irk.dto.applicants.IrkApplicantDto
import pl.poznan.ue.matriculation.irk.dto.applications.IrkApplicationDTO
import pl.poznan.ue.matriculation.irk.mapper.IrkApplicantMapper
import pl.poznan.ue.matriculation.irk.mapper.IrkApplicationMapper
import pl.poznan.ue.matriculation.irk.service.IrkService

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

    @Bean(name = ["DreamApplyService"])
    fun dreamApplyService(): DreamApplyService {
        return DreamApplyService(
                apiKey = dreamApplyApiKey,
                instanceUrl = dreamApplyInstanceUrl
        )
    }

    @Bean(name = ["DreamApplyApplicantDataSource"])
    fun dreamApplyApplicantDataSource(
            @Autowired @Qualifier("DreamApplyService") dreamApplyService: DreamApplyService
    ): IApplicationDataSource<DreamApplyApplicationDto, DreamApplyApplicantDto> {
        return DreamApplyDataSourceImpl(
                dreamApplyService = dreamApplyService,
                name = "Dream Apply",
                id = "DREAM_APPLY",
                applicantMapper = DreamApplyApplicantMapper(),
                applicationMapper = DreamApplyApplicationMapper()
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
}