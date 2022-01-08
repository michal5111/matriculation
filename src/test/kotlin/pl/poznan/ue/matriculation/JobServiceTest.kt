package pl.poznan.ue.matriculation

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.core.task.SyncTaskExecutor
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.applicantDataSources.IPhotoDownloader
import pl.poznan.ue.matriculation.kotlinExtensions.toByteArray
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.job.JobType
import pl.poznan.ue.matriculation.local.repo.ApplicationRepository
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.local.service.JobService
import pl.poznan.ue.matriculation.oracle.repo.PersonRepository
import java.util.*
import java.util.concurrent.Executor
import kotlin.test.assertTrue

@SpringBootTest
class JobServiceTest {

    val logger: Logger = LoggerFactory.getLogger(JobServiceTest::class.java)

    @Autowired
    lateinit var importService: ImportService

    @Autowired
    lateinit var jobService: JobService

    @Autowired
    lateinit var applicationRepository: ApplicationRepository

    @Autowired
    lateinit var personRepository: PersonRepository

    @Autowired
    lateinit var dataSourceFactory: ApplicationDataSourceFactory

    @TestConfiguration
    class TestConfig {
        @Bean(name = ["defaultTaskExecutor"])
        @Primary
        fun testTaskExecutor(): Executor {
            return SyncTaskExecutor()
        }
    }

    @Transactional(transactionManager = "oracleTransactionManager")
    @ParameterizedTest
    @CsvSource(
        "S3-SD,S3-SD_202021,s1-S3-SD,IRK_TEST,S3-SD",
//        "S1-RiFB,S1_PL_SZ_202122,s1-S1-RiFB,IRK_PRIMARY,S1-RiFB",
        //"S1-MSG,S1_PL_SZ_202122,s1-S1-MSG,IRK_PRIMARY,S1-MSG",
//        "S1-GT,S1_PL_SZ_202122,s1-S1-GT,IRK_PRIMARY,S1-GT",
//        "S1-Z,S1_PL_SZ_202122,s1-S1-Z,IRK_PRIMARY,S1-Z",
//        "S1-FAI,S1_PL_SZ_202122,s1-S1-FAI,IRK_PRIMARY,S1-FAI",
//        "S1-PS,S1_PL_SZ_202122,s1-S1-PS,IRK_PRIMARY,S1-PS",
//        "S1-P-E,S1_PL_SZ_202122,s1-S1-P-E,IRK_PRIMARY,S1-P-E",
//        "S1-E,S1_PL_SZ_202122,s1-S1-E,IRK_PRIMARY,S1-E",
        //"S1-IiE,S1_PL_SZ_202122,s1-S1-IiE,IRK_PRIMARY,S1-IiE",
//        "S1-ZIP,S1_PL_SZ_202122,s1-S1-ZIP,IRK_PRIMARY,S1-ZIP",
//        "S1-JiRP,S1_PL_SZ_202122,s1-S1-JiRP,IRK_PRIMARY,S1-JiRP",
//        "S1-Z,S1_NAWA_SZ_202122,s1-S1-Z,IRK_PRIMARY,S1-Z",
//        "S1-RiFB,S1_NAWA_SZ_202122,s1-S1-RiFB,IRK_PRIMARY,S1-RiFB",
//        "S1-MSG,S1_NAWA_SZ_202122,s1-S1-MSG,IRK_PRIMARY,S1-MSG",
//        "S1-IiE,S1_NAWA_SZ_202122,s1-S1-IiE,IRK_PRIMARY,S1-IiE",
//        "S1-FAI,S1_NAWA_SZ_202122,s1-S1-FAI,IRK_PRIMARY,S1-FAI",
//        "S1-E,S1_NAWA_SZ_202122,s1-S1-E,IRK_PRIMARY,S1-E"
    )
    fun test(
        programmeCode: String,
        registration: String,
        stageCode: String,
        dataSourceType: String,
        programmeForeignId: String
    ) {
        var import = importService.create(
            dateOfAddmision = Date(),
            didacticCycleCode = "202021/SL",
            indexPoolCode = "C",
            programmeCode = programmeCode,
            registration = registration,
            stageCode = stageCode,
            startDate = Date(),
            dataSourceType = dataSourceType,
            programmeForeignId = programmeForeignId,
            programmeForeignName = registration,
            indexPoolName = "Centralna"
        )
        jobService.runJob(JobType.IMPORT, import.id!!)
        jobService.runJob(JobType.IMPORT, import.id!!)
        logger.info("-------------------------------------------------------Koniec importu. Zapisywanie-----------")
        jobService.runJob(JobType.SAVE, import.id!!)
        import = importService.get(import.id!!)
        applicationRepository.findAll().forEach { application ->
            logger.info(application.id.toString())
            logger.info(application.stackTrace.orEmpty())
            assertEquals(null, application.importError)
            assertEquals(ApplicationImportStatus.IMPORTED, application.importStatus)
            val person = personRepository.findByIdOrNull(application.applicant?.usosId)
            Assertions.assertNotNull(person)
            person ?: throw IllegalArgumentException()
            Assertions.assertTrue(person.personProgrammes.any {
                it.programme.code == programmeCode
            }, "Should have programme $programmeCode")
            Assertions.assertTrue(person.personProgrammes.any { personProgramme ->
                personProgramme.personStages.any {
                    it.programmeStage.stage.code == stageCode
                }
            }, "Should have stage $stageCode")
            assertTrue(
                person.personPhoto?.photoBlob.toByteArray().contentEquals(
                    application.applicant?.photo?.let {
                        (dataSourceFactory.getDataSource(dataSourceType) as IPhotoDownloader).getPhoto(it)
                    }
                )
            )
        }
        assertEquals(ImportStatus.COMPLETE, import.importStatus)
    }
}
