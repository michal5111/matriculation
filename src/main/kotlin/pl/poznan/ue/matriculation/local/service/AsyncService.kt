package pl.poznan.ue.matriculation.local.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.irk.service.IrkService
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.repo.ImportProgressRepository
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Service
class AsyncService(
        private val importService: ImportService,
        private val importProgressRepository: ImportProgressRepository,
        private val irkService: IrkService,
        private val processService: ProcessService,
        private val importRepository: ImportRepository
) {

    val logger: Logger = LoggerFactory.getLogger(AsyncService::class.java)

    @PersistenceContext
    private lateinit var localEntityManager: EntityManager

    @Async
    fun importApplicantsAsync(importId: Long) {
        var import = importRepository.getOne(importId)
        var currentPage = 1
        var hasNext: Boolean
        var set = true
        try {
            do {
                logger.debug("Pobieram osoby...")
                val page = irkService.getApplications(
                        admitted = true,
                        paid = true,
                        programme = import.programmeCode,
                        registration = import.registration,
                        pageNumber = currentPage
                )
                logger.debug("Pobrałem osoby...")
                if (set) {
                    if (page.count == 0) {
                        throw IllegalStateException("Liczba kandydatów wynosi 0!")
                    }
                    import.importProgress!!.totalCount = page.count
                    importProgressRepository.save(import.importProgress!!)
                    set = false
                }
                logger.debug("Przetwarzam osoby...")
                page.results.forEach {
                    val application = processService.processApplication(importId, it)
                    localEntityManager.detach(application)
                }
                logger.debug("Przetworzyłem osoby...")
                hasNext = page.next != null
                currentPage++
            } while (hasNext)
            import = importService.get(importId)
            import.importProgress!!.importStatus = ImportStatus.IMPORTED
            importProgressRepository.save(import.importProgress!!)
        } catch (e: Exception) {
            throw ImportException(import.id!!, e.message, e)
        }
    }

    @Async
    fun savePersons(importId: Long) {
        val importDto = importRepository.getDtoById(importId)
        processService.processPersons(
                importId = importId,
                importDto = importDto
        )
        importService.setImportStatus(ImportStatus.COMPLETE, importId)
    }

}