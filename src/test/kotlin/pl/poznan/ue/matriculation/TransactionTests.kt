package pl.poznan.ue.matriculation

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pl.poznan.ue.matriculation.irk.service.IrkService
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import pl.poznan.ue.matriculation.local.service.AsyncService
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.local.service.ProcessService


@SpringBootTest
class TransactionTests {

    @Autowired
    lateinit var importRepository: ImportRepository

    @Autowired
    lateinit var irkService: IrkService

    @Autowired
    lateinit var importService: ImportService

    @Autowired
    lateinit var processService: ProcessService

    @Autowired
    lateinit var asyncService: AsyncService

    private val logger = LoggerFactory.getLogger(TransactionTests::class.java)

    lateinit var import: Import

    @BeforeEach
    fun before() {
        import = importService.create(
                dateOfAddmision = "2014-01-01T23:28:56.782Z",
                didacticCycleCode = "202021/SL",
                indexPoolCode = "C",
                programmeCode = "N2-ZIP",
                registration = "NZ2_T_ZiP_PL_sL_201920",
                stageCode = "s1-N2-ZIP",
                startDate = "2014-01-01T23:28:56.782Z"
        )
        import = importRepository.save(import)
        import = importRepository.getOne(import.id!!)
    }

    @Test
    //@Transactional
    fun import() {
        processService.processApplication(import.id!!, irkService.getApplication(1)!!)
        //asyncService.test(importId = import.id!!)
        //executor.threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS)
        import = importRepository.getOne(import.id!!)
        assert(import.id != null)
        //assert(import.applications.size > 0)
    }
}