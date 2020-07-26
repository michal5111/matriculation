package pl.poznan.ue.matriculation.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.poznan.ue.matriculation.applicantDataSources.IApplicationDataSource
import pl.poznan.ue.matriculation.applicantDataSources.IrkApplicationDataSourceImpl
import pl.poznan.ue.matriculation.irk.dto.applicants.ApplicantDto
import pl.poznan.ue.matriculation.irk.dto.applications.ApplicationDTO
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
    ): IApplicationDataSource<ApplicationDTO, ApplicantDto> {
        return IrkApplicationDataSourceImpl(
                id = "IRK_TEST",
                name = "Testowa IRK",
                irkService = irkService,
                setAsAccepted = testSetAsAccepted
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
    ): IApplicationDataSource<ApplicationDTO, ApplicantDto> {
        return IrkApplicationDataSourceImpl(
                id = "IRK_PRIMARY",
                name = "Główna IRK",
                irkService = irkService,
                setAsAccepted = primarySetAsAccepted
        )
    }
}