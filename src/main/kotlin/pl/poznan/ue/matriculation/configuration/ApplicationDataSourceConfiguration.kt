package pl.poznan.ue.matriculation.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.poznan.ue.matriculation.applicantDataSources.*
import pl.poznan.ue.matriculation.cem.domain.CemApplication
import pl.poznan.ue.matriculation.cem.domain.CemStudent
import pl.poznan.ue.matriculation.cem.service.CemApplicationService
import pl.poznan.ue.matriculation.cem.service.CemStudentService
import pl.poznan.ue.matriculation.cem.service.CourseService
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
import pl.poznan.ue.matriculation.oracle.repo.SchoolRepository
import pl.poznan.ue.matriculation.oracle.service.CitizenshipService
import pl.poznan.ue.matriculation.oracle.service.ProgrammeService
import pl.poznan.ue.matriculation.properties.*

@Configuration
class ApplicationDataSourceConfiguration {

    @Bean(name = ["IncomingService"])
    @ConditionalOnProperty(
        value = ["pl.poznan.ue.matriculation.incoming.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    fun incomingService(
        properties: IncomingProperties
    ): DreamApplyService {
        return DreamApplyService(
            apiKey = properties.instanceKey,
            instanceUrl = properties.instanceUrl
        )
    }

    @Bean(name = ["IncomingApplicantDataSource"])
    @ConditionalOnProperty(
        value = ["pl.poznan.ue.matriculation.incoming.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    fun incomingApplicantDataSource(
        @Autowired @Qualifier("IncomingService") IncomingService: DreamApplyService,
        @Autowired schoolRepository: SchoolRepository,
        properties: IncomingProperties
    ): IApplicationDataSource<DreamApplyApplicationDto, DreamApplyApplicantDto> {
        return IncomingDataSourceImpl(
            dreamApplyService = IncomingService,
            name = "Incoming",
            id = "INCOMING",
            applicantMapper = IncomingApplicantMapper(schoolRepository),
            applicationMapper = DreamApplyApplicationMapper(),
            status = properties.status
        )
    }

    @Bean(name = ["DreamApplyService"])
    @ConditionalOnProperty(
        value = ["pl.poznan.ue.matriculation.dreamApply.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    fun dreamApplyService(
        properties: DreamApplyProperties
    ): DreamApplyService {
        return DreamApplyService(
            apiKey = properties.instanceKey,
            instanceUrl = properties.instanceUrl
        )
    }

    @Bean(name = ["DreamApplyApplicantDataSource"])
    @ConditionalOnProperty(
        value = ["pl.poznan.ue.matriculation.dreamApply.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    fun dreamApplyApplicantDataSource(
        @Autowired @Qualifier("DreamApplyService") dreamApplyService: DreamApplyService,
        @Autowired schoolRepository: SchoolRepository,
        properties: DreamApplyProperties
    ): IApplicationDataSource<DreamApplyApplicationDto, DreamApplyApplicantDto> {
        return DreamApplyDataSourceImpl(
            dreamApplyService = dreamApplyService,
            name = "Dream Apply",
            id = "DREAM_APPLY",
            applicantMapper = DreamApplyApplicantMapper(schoolRepository),
            applicationMapper = DreamApplyApplicationMapper(),
            status = properties.status
        )
    }

    @Bean(name = ["TestIrkService"])
    @ConditionalOnProperty(
        value = ["pl.poznan.ue.matriculation.irk.test.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    fun testIrkService(
        properties: IrkTestProperties
    ): IrkService {
        return IrkService(
            properties.instanceUrl,
            properties.instanceKey
        )
    }

    @Bean(name = ["testIrkApplicantDataSource"])
    @ConditionalOnProperty(
        value = ["pl.poznan.ue.matriculation.irk.test.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    fun testIrkApplicantDataSource(
        @Autowired @Qualifier("TestIrkService") irkService: IrkService,
        properties: IrkTestProperties
    ): IApplicationDataSource<IrkApplicationDTO, IrkApplicantDto> {
        return IrkApplicationDataSourceImpl(
            id = "IRK_TEST",
            name = "Testowa IRK",
            irkService = irkService,
            setAsAccepted = properties.setAsAccepted,
            irkApplicantMapper = IrkApplicantMapper(),
            irkApplicationMapper = IrkApplicationMapper()
        )
    }

    @Bean(name = ["primaryIrkService"])
    @ConditionalOnProperty(
        value = ["pl.poznan.ue.matriculation.irk.primary.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    fun primaryIrkService(
        properties: IrkPrimaryProperties
    ): IrkService {
        return IrkService(
            properties.instanceUrl,
            properties.instanceKey
        )
    }

    @Bean(name = ["primaryIrkApplicantDataSource"])
    @ConditionalOnProperty(
        value = ["pl.poznan.ue.matriculation.irk.primary.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    fun primaryIrkApplicantDataSource(
        @Autowired @Qualifier("primaryIrkService") irkService: IrkService,
        properties: IrkPrimaryProperties
    ): IApplicationDataSource<IrkApplicationDTO, IrkApplicantDto> {
        return IrkApplicationDataSourceImpl(
            id = "IRK_PRIMARY",
            name = "Główna IRK",
            irkService = irkService,
            setAsAccepted = properties.setAsAccepted,
            irkApplicationMapper = IrkApplicationMapper(),
            irkApplicantMapper = IrkApplicantMapper()
        )
    }

    @Bean(name = ["excelFileApplicantDAtaSource"])
    fun excelFileApplicantDataSource(
        programmeService: ProgrammeService
    ): IApplicationDataSource<ExcelFileApplicationDto, ExcelFileApplicantDto> {
        return ExcelFileDataSourceImpl(
            excelFileApplicantMapper = ExcelFileApplicantMapper(),
            excelFileApplicationMapper = ExcelFileApplicationMapper(),
            programmeService = programmeService
        )
    }

    @Bean(name = ["cemApplicationDataSource"])
    @ConditionalOnProperty(
        value = ["pl.poznan.ue.matriculation.cem.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    fun cemApplicationDataSource(
        cemApplicationService: CemApplicationService,
        cemStudentService: CemStudentService,
        cemCourseService: CourseService,
        programmeService: ProgrammeService,
        citizenshipService: CitizenshipService,
        properties: CemDatasourceProperties
    ): IApplicationDataSource<CemApplication, CemStudent> {
        return CemApplicationDataSourceImpl(
            id = "CEM",
            name = "CEM",
            instanceUrl = properties.instanceUrl,
            cemApplicationService = cemApplicationService,
            cemStudentService = cemStudentService,
            courseService = cemCourseService,
            programmeService = programmeService,
            applicationStatus = properties.applicationStatus,
            citizenshipService = citizenshipService
        )
    }
}
