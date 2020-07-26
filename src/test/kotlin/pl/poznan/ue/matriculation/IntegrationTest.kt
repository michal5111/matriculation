//package pl.poznan.ue.matriculation
//
//import org.junit.Assert
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
//import pl.poznan.ue.matriculation.local.repo.ApplicationRepository
//import pl.poznan.ue.matriculation.local.repo.ImportRepository
//import pl.poznan.ue.matriculation.local.service.AsyncService
//import pl.poznan.ue.matriculation.local.service.ImportService
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
//    private lateinit var asyncService: AsyncService
//
//    @Autowired
//    private lateinit var importRepository: ImportRepository
//
//    @Autowired
//    private lateinit var applicationRepository: ApplicationRepository
//
//    private val logger = LoggerFactory.getLogger(IntegrationTest::class.java)
//
//    @Test
//    fun testImport() {
//        importService.create(
//                dateOfAddmision = "2014-01-01T23:28:56.782Z",
//                didacticCycleCode = "202021/SL",
//                indexPoolCode = "C",
//                programmeCode = "S1-E",
//                registration = "S1_PL_SZ_202021",
//                stageCode = "s1-S1-E",
//                startDate = "2014-01-01T23:28:56.782Z",
//                dataSourceType = "IRK_TEST"
//        )
//        importService.create(
//                dateOfAddmision = "2014-01-01T23:28:56.782Z",
//                didacticCycleCode = "202021/SL",
//                indexPoolCode = "C",
//                programmeCode = "S1-FAI",
//                registration = "S1_PL_SZ_202021",
//                stageCode = "s1-S1-FAI",
//                startDate = "2014-01-01T23:28:56.782Z",
//                dataSourceType = "IRK_TEST"
//        )
//        importService.create(
//                dateOfAddmision = "2014-01-01T23:28:56.782Z",
//                didacticCycleCode = "202021/SL",
//                indexPoolCode = "C",
//                programmeCode = "S1-GT",
//                registration = "S1_PL_SZ_202021",
//                stageCode = "s1-S1-GT",
//                startDate = "2014-01-01T23:28:56.782Z",
//                dataSourceType = "IRK_TEST"
//        )
//        importService.create(
//                dateOfAddmision = "2014-01-01T23:28:56.782Z",
//                didacticCycleCode = "202021/SL",
//                indexPoolCode = "C",
//                programmeCode = "S1-IiE",
//                registration = "S1_PL_SZ_202021",
//                stageCode = "s1-S1-IiE",
//                startDate = "2014-01-01T23:28:56.782Z",
//                dataSourceType = "IRK_TEST"
//        )
//        importService.create(
//                dateOfAddmision = "2014-01-01T23:28:56.782Z",
//                didacticCycleCode = "202021/SL",
//                indexPoolCode = "C",
//                programmeCode = "S1-MSG",
//                registration = "S1_PL_SZ_202021",
//                stageCode = "s1-S1-MSG",
//                startDate = "2014-01-01T23:28:56.782Z",
//                dataSourceType = "IRK_TEST"
//        )
//        importService.create(
//                dateOfAddmision = "2014-01-01T23:28:56.782Z",
//                didacticCycleCode = "202021/SL",
//                indexPoolCode = "C",
//                programmeCode = "S1-P-E",
//                registration = "S1_PL_SZ_202021",
//                stageCode = "s1-S1-P-E",
//                startDate = "2014-01-01T23:28:56.782Z",
//                dataSourceType = "IRK_TEST"
//        )
//        importService.create(
//                dateOfAddmision = "2014-01-01T23:28:56.782Z",
//                didacticCycleCode = "202021/SL",
//                indexPoolCode = "C",
//                programmeCode = "S1-PS",
//                registration = "S1_PL_SZ_202021",
//                stageCode = "s1-S1-PS",
//                startDate = "2014-01-01T23:28:56.782Z",
//                dataSourceType = "IRK_TEST"
//        )
//        importService.create(
//                dateOfAddmision = "2014-01-01T23:28:56.782Z",
//                didacticCycleCode = "202021/SL",
//                indexPoolCode = "C",
//                programmeCode = "S1-RiFB",
//                registration = "S1_PL_SZ_202021",
//                stageCode = "s1-S1-RiFB",
//                startDate = "2014-01-01T23:28:56.782Z",
//                dataSourceType = "IRK_TEST"
//        )
//        importService.create(
//                dateOfAddmision = "2014-01-01T23:28:56.782Z",
//                didacticCycleCode = "202021/SL",
//                indexPoolCode = "C",
//                programmeCode = "S1-Z",
//                registration = "S1_PL_SZ_202021",
//                stageCode = "s1-S1-Z",
//                startDate = "2014-01-01T23:28:56.782Z",
//                dataSourceType = "IRK_TEST"
//        )
//        importService.create(
//                dateOfAddmision = "2014-01-01T23:28:56.782Z",
//                didacticCycleCode = "202021/SL",
//                indexPoolCode = "C",
//                programmeCode = "S1-ZIP",
//                registration = "S1_PL_SZ_202021",
//                stageCode = "s1-S1-ZIP",
//                startDate = "2014-01-01T23:28:56.782Z",
//                dataSourceType = "IRK_TEST"
//        )
//        var allImports = importRepository.findAll()
//        allImports.forEach {
//            asyncService.importApplicantsAsync(it.id!!)
//            asyncService.savePersons(it.id!!)
//        }
//        allImports = importRepository.findAll()
//        allImports.forEach {
//            Assert.assertEquals(ImportStatus.COMPLETE, it.importProgress!!.importStatus)
////            it.applications.forEach { application ->
////                Assert.assertEquals(ApplicationImportStatus.IMPORTED, application.importStatus)
////            }
//        }
//        val allApplications = applicationRepository.findAll()
//        allApplications.forEach {
//            if (it.importStatus == ApplicationImportStatus.ERROR) {
//                logger.info("Import error: ${it.foreignId}\nStackTrace: ${it.importError}")
//            }
//            Assert.assertEquals(ApplicationImportStatus.IMPORTED, it.importStatus)
//        }
//        allImports.forEach {
//            if (it.importProgress!!.totalCount != it.importProgress!!.importedApplications) {
//                logger.info("Imported applications count not equals total count: ${it.programmeCode} Total count: ${it.importProgress!!.totalCount} Saved: ${it.importProgress!!.importedApplications}")
//            }
//            if (it.importProgress!!.totalCount != it.importProgress!!.savedApplicants) {
//                logger.info("Saved applications count not equals total count: ${it.programmeCode} Total count: ${it.importProgress!!.totalCount} Saved: ${it.importProgress!!.savedApplicants}")
//            }
//            Assert.assertEquals(it.importProgress!!.totalCount, it.importProgress!!.importedApplications)
//            Assert.assertEquals(it.importProgress!!.totalCount, it.importProgress!!.savedApplicants)
//        }
//        logger.info("Total count sum: ${allImports.sumBy { it.importProgress!!.totalCount!! }} Applicants sum = ${allApplications.count()}")
//        Assert.assertEquals(allImports.sumBy { it.importProgress!!.totalCount!! }, allApplications.count())
//    }
//
//    @Test
//    fun testImport2() {
//        importService.create(
//                dateOfAddmision = "2014-01-01T23:28:56.782Z",
//                didacticCycleCode = "202021/SL",
//                indexPoolCode = "C",
//                programmeCode = "S1-E",
//                registration = "S1_PL_SZ_202021",
//                stageCode = "s1-S1-E",
//                startDate = "2014-01-01T23:28:56.782Z",
//                dataSourceType = "IRK_TEST"
//        )
//        var allImports = importRepository.findAll()
//        allImports.forEach {
//            asyncService.importApplicantsAsync(it.id!!)
//            asyncService.savePersons(it.id!!)
//        }
//        allImports = importRepository.findAll()
//        allImports.forEach {
//            Assert.assertEquals(ImportStatus.COMPLETE, it.importProgress!!.importStatus)
////            it.applications.forEach { application ->
////                Assert.assertEquals(ApplicationImportStatus.IMPORTED, application.importStatus)
////            }
//        }
//        val allApplications = applicationRepository.findAll()
//        allApplications.forEach {
//            logger.info("Import error: ${it.foreignId}\nStackTrace: ${it.importError}")
//            Assert.assertEquals(ApplicationImportStatus.IMPORTED, it.importStatus)
//        }
//    }
//
//    @Test
//    fun testImport3() {
////        importService.create(
////                dateOfAddmision = "2014-01-01T23:28:56.782Z",
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-E",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-E",
////                startDate = "2014-01-01T23:28:56.782Z"
////        )
////        importService.create(
////                dateOfAddmision = "2014-01-01T23:28:56.782Z",
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-FAI",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-FAI",
////                startDate = "2014-01-01T23:28:56.782Z"
////        )
////        importService.create(
////                dateOfAddmision = "2014-01-01T23:28:56.782Z",
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-GT",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-GT",
////                startDate = "2014-01-01T23:28:56.782Z"
////        )
////        importService.create(
////                dateOfAddmision = "2014-01-01T23:28:56.782Z",
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-IiE",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-IiE",
////                startDate = "2014-01-01T23:28:56.782Z"
////        )
////        importService.create(
////                dateOfAddmision = "2014-01-01T23:28:56.782Z",
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-MSG",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-MSG",
////                startDate = "2014-01-01T23:28:56.782Z"
////        )
////        importService.create(
////                dateOfAddmision = "2014-01-01T23:28:56.782Z",
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-P-E",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-P-E",
////                startDate = "2014-01-01T23:28:56.782Z"
////        )
////        importService.create(
////                dateOfAddmision = "2014-01-01T23:28:56.782Z",
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-PS",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-PS",
////                startDate = "2014-01-01T23:28:56.782Z"
////        )
////        importService.create(
////                dateOfAddmision = "2014-01-01T23:28:56.782Z",
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-RiFB",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-RiFB",
////                startDate = "2014-01-01T23:28:56.782Z"
////        )
////        importService.create(
////                dateOfAddmision = "2014-01-01T23:28:56.782Z",
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-Z",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-Z",
////                startDate = "2014-01-01T23:28:56.782Z"
////        )
////        importService.create(
////                dateOfAddmision = "2014-01-01T23:28:56.782Z",
////                didacticCycleCode = "202021/SL",
////                indexPoolCode = "C",
////                programmeCode = "S1-ZIP",
////                registration = "S1_PL_SZ_202021",
////                stageCode = "s1-S1-ZIP",
////                startDate = "2014-01-01T23:28:56.782Z"
////        )
//        var allImports = importRepository.findAll()
//        allImports.forEach {
//            asyncService.importApplicantsAsync(it.id!!)
//        }
//        allImports = importRepository.findAll()
//        allImports.forEach {
//            Assert.assertEquals(ImportStatus.IMPORTED, it.importProgress!!.importStatus)
//        }
//        val allApplications = applicationRepository.findAll()
//        allApplications.forEach {
//            Assert.assertEquals(ApplicationImportStatus.NOT_IMPORTED, it.importStatus)
//        }
//        allImports.forEach {
//            if (it.importProgress!!.totalCount != it.importProgress!!.importedApplications) {
//                logger.info("Imported applications count not equals total count: ${it.programmeCode} Total count: ${it.importProgress!!.totalCount} Saved: ${it.importProgress!!.importedApplications}")
//            }
//            Assert.assertEquals(it.importProgress!!.totalCount, it.importProgress!!.importedApplications)
//        }
//        logger.info("Total count sum: ${allImports.sumBy { it.importProgress!!.totalCount!! }} Applicants sum = ${allApplications.count()}")
//        //Assert.assertEquals(allImports.sumBy { it.importProgress!!.totalCount!! }, allApplications.count())
//    }
//}