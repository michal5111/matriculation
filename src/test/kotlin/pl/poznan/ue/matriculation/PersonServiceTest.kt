//package pl.poznan.ue.matriculation
//
//import org.junit.jupiter.api.Test
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.context.TestConfiguration
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Primary
//import org.springframework.core.task.SyncTaskExecutor
//import org.springframework.orm.ObjectOptimisticLockingFailureException
//import pl.poznan.ue.matriculation.irk.mapper.IrkApplicantMapper
//import pl.poznan.ue.matriculation.irk.mapper.IrkApplicationMapper
//import pl.poznan.ue.matriculation.irk.service.IrkService
//import pl.poznan.ue.matriculation.local.repo.ImportRepository
//import pl.poznan.ue.matriculation.local.service.ImportService
//import pl.poznan.ue.matriculation.oracle.repo.PersonRepository
//import pl.poznan.ue.matriculation.oracle.service.PersonService
//import java.util.*
//import java.util.concurrent.Executor
//
//@SpringBootTest
//class PersonServiceTest {
//
//    val logger: Logger = LoggerFactory.getLogger(PersonServiceTest::class.java)
//
//    val irkApplicantMapper = IrkApplicantMapper()
//
//    @Autowired
//    lateinit var importService: ImportService
//
//    @Autowired
//    lateinit var importRepository: ImportRepository
//
//    @Qualifier("TestIrkService")
//    @Autowired
//    lateinit var irkService: IrkService
//
//    @Autowired
//    lateinit var personService: PersonService
//
//    val irkApplicationMapper = IrkApplicationMapper()
//
//    @Autowired
//    lateinit var personRepository: PersonRepository
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
//    @Test
//    fun test() {
//        val import = importService.create(
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
//        val importDto = import.id?.let { importRepository.getDtoById(it) }!!
//        val irkApplication = irkService.getApplication(2159)!!
//        //val irkApplication = irkService.getApplication(1757)!!
//        val irkApplicant = irkService.getApplicantById(irkApplication.foreignApplicantId)
//        val application = irkApplicationMapper.mapApplicationDtoToApplication(irkApplication).also {
//            it.dataSourceId = "IRK_TEST"
//        }
//        val applicant = irkApplicant?.let { irkApplicantMapper.mapApplicantDtoToApplicant(it) }!!.also {
//            it.dataSourceId = "IRK_TEST"
//        }
//        application.applicant = applicant
//        try {
//            personService.process(application, importDto) {
//                1
//            }
//        } catch (e: ObjectOptimisticLockingFailureException) {
//            val person = (e.identifier as Long?)?.let { personRepository.getById(it) }
//            logger.info("Exception Person: {}", person?.modificationDate)
//            throw e
//        }
//    }
//}