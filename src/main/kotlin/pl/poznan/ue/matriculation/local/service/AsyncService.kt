package pl.poznan.ue.matriculation.local.service

import org.hibernate.JDBCException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.irk.service.IrkService
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.repo.ApplicantRepository
import pl.poznan.ue.matriculation.local.repo.ApplicationRepository
import pl.poznan.ue.matriculation.local.repo.ImportProgressRepository
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import java.util.concurrent.Future

@Service
class AsyncService(
        private val importService: ImportService,
        private val importProgressRepository: ImportProgressRepository,
        private val irkService: IrkService,
        private val processService: ProcessService,
        private val importRepository: ImportRepository,
        private val applicationRepository: ApplicationRepository,
        private val applicantRepository: ApplicantRepository
) {

    @Async
    fun importApplicantsAsync(importId: Long): Future<Int> {
        var import = importRepository.getOne(importId)
        var currentPage = 1
        var hasNext: Boolean
        var set = true
        try {
            do {
                val page = irkService.getApplications(
                        admitted = true,
                        paid = true,
                        programme = import.programmeCode,
                        registration = import.registration,
                        pageNumber = currentPage
                )
                if (set) {
                    if (page.count == 0) {
                        throw ImportException(importId, "Liczba kandydatów wynosi 0!")
                    }
                    import.importProgress!!.totalCount = page.count
                    importProgressRepository.save(import.importProgress!!)
                    set = false
                }
                page.results.forEach {
                    processService.processApplication(importId, it)
                }
                hasNext = page.next != null
                currentPage++
            } while (hasNext)
            import = importService.get(importId)
            import.importProgress!!.importStatus = ImportStatus.IMPORTED
            importProgressRepository.save(import.importProgress!!)
        } catch (e: java.lang.Exception) {
            throw ImportException(import.id!!, e.message, e)
        }
        return AsyncResult(import.importProgress!!.importedApplications)
    }

    @Async
    fun savePersons(importId: Long): Future<Int> {
        val importProgress = importProgressRepository.getOne(importId)
        var currentPage = 0
        var savedApplicants = 0
        var importErrors = 0
        do {
            val applicationsPage: Page<Application> = applicationRepository.getAllByImportIdAndApplicationImportStatus(PageRequest.of(currentPage, 56), importId)
            applicationsPage.content.forEach {
                try {
                    processService.processPerson(it, importId)
                    savedApplicants++
                    importProgress.savedApplicants = savedApplicants
                } catch (e: JDBCException) {
                    it.applicationImportStatus = ApplicationImportStatus.ERROR
                    it.importError = "${e.javaClass.simpleName}: ${e.message} \n Error code: ${e.errorCode} " +
                            "Sql exception: ${e.sqlException} \n " +
                            "Sql: ${e.sql} \n " +
                            "Sql state: ${e.sqlState}"
                    it.stackTrace = e.stackTrace.joinToString("\n", "\nStackTrace: ")
                    importErrors++
                    importProgress.saveErrors = importErrors
                } catch (e: Exception) {
                    if (e.cause is JDBCException) {
                        val ex: JDBCException = e.cause as JDBCException
                        it.applicationImportStatus = ApplicationImportStatus.ERROR
                        it.importError = "${e.javaClass.simpleName}: ${e.message} \n Error code: ${ex.errorCode} " +
                                "Sql exception: ${ex.sqlException} \n " +
                                "Sql: ${ex.sql} \n " +
                                "Sql state: ${ex.sqlState} "
                        it.stackTrace = e.stackTrace.joinToString("\n", "\nStackTrace: ")
                        importErrors++
                        importProgress.saveErrors = importErrors
                    } else {
                        it.applicationImportStatus = ApplicationImportStatus.ERROR
                        it.importError = "${e.javaClass.simpleName}: ${e.message}"
                        it.stackTrace = e.stackTrace.joinToString("\n", "\nStackTrace: ")
                        importErrors++
                        importProgress.saveErrors = importErrors
                    }
                }
                importProgressRepository.saveAndFlush(importProgress)
                applicantRepository.save(it.applicant!!)
                applicationRepository.save(it)
            }
            currentPage++
        } while (applicationsPage.hasNext())
        importProgress.importStatus = ImportStatus.COMPLETE
        importProgressRepository.save(importProgress)
        return AsyncResult(importProgress.savedApplicants)
    }
}