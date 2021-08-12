//package pl.poznan.ue.matriculation
//
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.context.TestConfiguration
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Primary
//import org.springframework.core.task.SyncTaskExecutor
//import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
//import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
//import pl.poznan.ue.matriculation.local.job.JobType
//import pl.poznan.ue.matriculation.local.repo.ApplicationRepository
//import pl.poznan.ue.matriculation.local.repo.ImportProgressRepository
//import pl.poznan.ue.matriculation.local.repo.ImportRepository
//import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
//import pl.poznan.ue.matriculation.local.service.ImportService
//import pl.poznan.ue.matriculation.local.service.JobService
//import pl.poznan.ue.matriculation.local.service.ProcessService
//import java.util.*
//import java.util.concurrent.Executor
//
//@SpringBootTest
//class IntegrationTest {
//
//    @TestConfiguration
//    class TestConfig {
//        @Bean
//        @Primary
//        fun testTaskExecutor(): Executor {
//            return SyncTaskExecutor()
//        }
//    }
//
//    @Autowired
//    private lateinit var importService: ImportService
//
//    @Autowired
//    private lateinit var importRepository: ImportRepository
//
//    @Autowired
//    private lateinit var applicationRepository: ApplicationRepository
//
//    @Autowired
//    private lateinit var importProgressRepository: ImportProgressRepository
//
//    @Autowired
//    private lateinit var jobService: JobService
//
//    @Autowired
//    private lateinit var processService: ProcessService
//
//    @Autowired
//    private lateinit var applicationDataSourceFactory: ApplicationDataSourceFactory
//
//    private val logger = LoggerFactory.getLogger(IntegrationTest::class.java)
//
//    @Test
//    fun testImport() {
//        importService.create(
//            dateOfAddmision = Date(),
//            didacticCycleCode = "202021/SL",
//            indexPoolCode = "C",
//            programmeCode = "S1-E",
//            registration = "S1_PL_SZ_202021",
//            stageCode = "s1-S1-E",
//            startDate = Date(),
//            dataSourceType = "IRK_TEST",
//            programmeForeignId = "S1-E"
//        )
//        importService.create(
//            dateOfAddmision = Date(),
//            didacticCycleCode = "202021/SL",
//            indexPoolCode = "C",
//            programmeCode = "S1-FAI",
//            registration = "S1_PL_SZ_202021",
//            stageCode = "s1-S1-FAI",
//            startDate = Date(),
//            dataSourceType = "IRK_TEST",
//            programmeForeignId = "S1-FAI"
//        )
//        importService.create(
//            dateOfAddmision = Date(),
//            didacticCycleCode = "202021/SL",
//            indexPoolCode = "C",
//            programmeCode = "S1-GT",
//            registration = "S1_PL_SZ_202021",
//            stageCode = "s1-S1-GT",
//            startDate = Date(),
//            dataSourceType = "IRK_TEST",
//            programmeForeignId = "S1-GT"
//        )
//        importService.create(
//            dateOfAddmision = Date(),
//            didacticCycleCode = "202021/SL",
//            indexPoolCode = "C",
//            programmeCode = "S1-IiE",
//            registration = "S1_PL_SZ_202021",
//            stageCode = "s1-S1-IiE",
//            startDate = Date(),
//            dataSourceType = "IRK_TEST",
//            programmeForeignId = "S1-IiE"
//        )
//        importService.create(
//            dateOfAddmision = Date(),
//            didacticCycleCode = "202021/SL",
//            indexPoolCode = "C",
//            programmeCode = "S1-MSG",
//            registration = "S1_PL_SZ_202021",
//            stageCode = "s1-S1-MSG",
//            startDate = Date(),
//            dataSourceType = "IRK_TEST",
//            programmeForeignId = "S1-MSG"
//        )
//        importService.create(
//            dateOfAddmision = Date(),
//            didacticCycleCode = "202021/SL",
//            indexPoolCode = "P-E",
//            programmeCode = "S1-P-E",
//            registration = "S1_PL_SZ_202021",
//            stageCode = "s1-S1-P-E",
//            startDate = Date(),
//            dataSourceType = "IRK_TEST",
//            programmeForeignId = "S1-P-E"
//        )
//        importService.create(
//            dateOfAddmision = Date(),
//            didacticCycleCode = "202021/SL",
//            indexPoolCode = "C",
//            programmeCode = "S1-PS",
//            registration = "S1_PL_SZ_202021",
//            stageCode = "s1-S1-PS",
//            startDate = Date(),
//            dataSourceType = "IRK_TEST",
//            programmeForeignId = "S1-PS"
//        )
//        importService.create(
//            dateOfAddmision = Date(),
//            didacticCycleCode = "202021/SL",
//            indexPoolCode = "C",
//            programmeCode = "S1-RiFB",
//            registration = "S1_PL_SZ_202021",
//            stageCode = "s1-S1-RiFB",
//            startDate = Date(),
//            dataSourceType = "IRK_TEST",
//            programmeForeignId = "S1-RiFB"
//        )
//        importService.create(
//            dateOfAddmision = Date(),
//            didacticCycleCode = "202021/SL",
//            indexPoolCode = "C",
//            programmeCode = "S1-Z",
//            registration = "S1_PL_SZ_202021",
//            stageCode = "s1-S1-Z",
//            startDate = Date(),
//            dataSourceType = "IRK_TEST",
//            programmeForeignId = "S1-Z"
//        )
//        importService.create(
//            dateOfAddmision = Date(),
//            didacticCycleCode = "202021/SL",
//            indexPoolCode = "C",
//            programmeCode = "S1-ZIP",
//            registration = "S1_PL_SZ_202021",
//            stageCode = "s1-S1-ZIP",
//            startDate = Date(),
//            dataSourceType = "IRK_TEST",
//            programmeForeignId = "S1-ZIP"
//        )
//        var allImports = importRepository.findAll()
//        allImports.forEach {
//            jobService.runJob(JobType.IMPORT, it.id!!)
//            jobService.runJob(JobType.SAVE, it.id!!)
//        }
//        allImports = importRepository.findAll()
//        allImports.forEach { import ->
//            val applications = applicationRepository.findAllByImportId(importId = import.id!!)
//            applications.filter {
//                it.importStatus != ApplicationImportStatus.IMPORTED
//            }.forEach {
//                logger.error("Apllication ${it.foreignId} error ${it.importError}")
//            }
//        }
//        allImports.forEach {
//            assertEquals(ImportStatus.COMPLETE, it.importProgress.importStatus)
////            it.applications.forEach { application ->
////                assertEquals(ApplicationImportStatus.IMPORTED, application.importStatus)
////            }
//        }
//        val allApplications = applicationRepository.findAll()
//        allApplications.forEach {
//            if (it.importStatus == ApplicationImportStatus.ERROR) {
//                logger.info("Import error: ${it.foreignId}\nStackTrace: ${it.importError}")
//            }
//            assertEquals(ApplicationImportStatus.IMPORTED, it.importStatus)
//        }
//        allImports.forEach {
//            if (it.importProgress.totalCount != it.importProgress.importedApplications) {
//                logger.info("Imported applications count not equals total count: ${it.programmeCode} Total count: ${it.importProgress.totalCount} Saved: ${it.importProgress.importedApplications}")
//            }
//            if (it.importProgress.totalCount != it.importProgress.savedApplicants) {
//                logger.info("Saved applications count not equals total count: ${it.programmeCode} Total count: ${it.importProgress.totalCount} Saved: ${it.importProgress.savedApplicants}")
//            }
//            assertEquals(it.importProgress.totalCount, it.importProgress.importedApplications)
//            assertEquals(it.importProgress.totalCount, it.importProgress.savedApplicants)
//        }
//        logger.info("Total count sum: ${allImports.sumBy { it.importProgress.totalCount!! }} Applicants sum = ${allApplications.count()}")
//        assertEquals(allImports.sumOf { it.importProgress.totalCount!! }, allApplications.count())
//    }
//
//    @Test
//    fun testImport2() {
//        importService.create(
//            dateOfAddmision = Date(),
//            didacticCycleCode = "202021/SL",
//            indexPoolCode = "C",
//            programmeCode = "S3-SD",
//            registration = "S3-SD_202021",
//            stageCode = "s1-S3-SD",
//            startDate = Date(),
//            dataSourceType = "IRK_TEST",
//            programmeForeignId = "S3-SD"
//        )
//        var allImports = importRepository.findAll()
//        allImports.forEach {
//            jobService.runJob(JobType.IMPORT,it.id!!)
//            jobService.runJob(JobType.SAVE,it.id!!)
//        }
//        allImports = importRepository.findAll()
//        allImports.forEach {
//            assertEquals(ImportStatus.COMPLETE, it.importProgress.importStatus)
////            it.applications.forEach { application ->
////                assertEquals(ApplicationImportStatus.IMPORTED, application.importStatus)
////            }
//        }
//        val allApplications = applicationRepository.findAll()
//        allApplications.forEach {
//            logger.info("Import error: ${it.foreignId}\nStackTrace: ${it.importError}")
//            assertEquals(ApplicationImportStatus.IMPORTED, it.importStatus)
//        }
//    }
//
//    @Test
//    fun testImport3() {
////        importService.create(
////                dateOfAddmision = Date(),
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-E",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-E",
////                startDate = Date()
////        )
////        importService.create(
////                dateOfAddmision = Date(),
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-FAI",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-FAI",
////                startDate = Date()
////        )
////        importService.create(
////                dateOfAddmision = Date(),
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-GT",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-GT",
////                startDate = Date()
////        )
////        importService.create(
////                dateOfAddmision = Date(),
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-IiE",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-IiE",
////                startDate = Date()
////        )
////        importService.create(
////                dateOfAddmision = Date(),
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-MSG",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-MSG",
////                startDate = Date()
////        )
////        importService.create(
////                dateOfAddmision = Date(),
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-P-E",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-P-E",
////                startDate = Date()
////        )
////        importService.create(
////                dateOfAddmision = Date(),
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-PS",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-PS",
////                startDate = Date()
////        )
////        importService.create(
////                dateOfAddmision = Date(),
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-RiFB",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-RiFB",
////                startDate = Date()
////        )
////        importService.create(
////                dateOfAddmision = Date(),
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-Z",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-Z",
////                startDate = Date()
////        )
////        importService.create(
////                dateOfAddmision = Date(),
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-ZIP",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-ZIP",
////                startDate = Date()
////        )
//        var allImports = importRepository.findAll()
//        allImports.forEach {
//            jobService.runJob(JobType.IMPORT,it.id!!)
//        }
//        allImports = importRepository.findAll()
//        allImports.forEach {
//            assertEquals(ImportStatus.IMPORTED, it.importProgress.importStatus)
//        }
//        val allApplications = applicationRepository.findAll()
//        allApplications.forEach {
//            assertEquals(ApplicationImportStatus.NOT_IMPORTED, it.importStatus)
//        }
//        allImports.forEach {
//            if (it.importProgress.totalCount != it.importProgress.importedApplications) {
//                logger.info("Imported applications count not equals total count: ${it.programmeCode} Total count: ${it.importProgress.totalCount} Saved: ${it.importProgress.importedApplications}")
//            }
//            assertEquals(it.importProgress.totalCount, it.importProgress.importedApplications)
//        }
//        logger.info("Total count sum: ${allImports.sumOf { it.importProgress.totalCount!! }} Applicants sum = ${allApplications.count()}")
//        //assertEquals(allImports.sumBy { it.importProgress!!.totalCount!! }, allApplications.count())
//    }
//}