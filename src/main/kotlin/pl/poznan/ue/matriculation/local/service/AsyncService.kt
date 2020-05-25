package pl.poznan.ue.matriculation.local.service

import org.hibernate.JDBCException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.irk.service.IrkService
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.repo.ApplicationRepository
import pl.poznan.ue.matriculation.local.repo.ImportProgressRepository
import pl.poznan.ue.matriculation.local.repo.ImportRepository

@Service
class AsyncService(
        private val importService: ImportService,
        private val importProgressRepository: ImportProgressRepository,
        private val irkService: IrkService,
        private val processService: ProcessService,
        private val importRepository: ImportRepository,
        private val applicationRepository: ApplicationRepository
) {

    val logger: Logger = LoggerFactory.getLogger(AsyncService::class.java)

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
                        throw ImportException(importId, "Liczba kandydatów wynosi 0!")
                    }
                    import.importProgress!!.totalCount = page.count
                    importProgressRepository.save(import.importProgress!!)
                    set = false
                }
                logger.debug("Przetwarzam osoby...")
                page.results.forEach {
                    processService.processApplication(importId, it)
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
        val pageRequest: Pageable = PageRequest.of(0, 20)
        var applicationsPage: Page<Application> = applicationRepository.getAllByImportIdAndApplicationImportStatus(pageRequest, importId)
        while (!applicationsPage.isEmpty) {
            applicationsPage.content.forEach {
                try {
                    processService.processPerson(it, importId)
                } catch (e: JDBCException) {
                    processService.handleSaveJdbcException(e, it, importId)
                } catch (e: Exception) {
                    processService.handleSaveException(e, it, importId)
                }
            }
            applicationsPage = applicationRepository.getAllByImportIdAndApplicationImportStatus(pageRequest, importId)
        }
        processService.setSaveComplete(importId)
    }
}