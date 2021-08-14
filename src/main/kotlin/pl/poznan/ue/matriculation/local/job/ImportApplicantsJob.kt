package pl.poznan.ue.matriculation.local.job

import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.ApplicantsImportStartCondition
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.repo.ImportProgressRepository
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.local.service.ProcessService

class ImportApplicantsJob(
    private val importRepository: ImportRepository,
    private val importProgressRepository: ImportProgressRepository,
    private val processService: ProcessService,
    private val importService: ImportService,
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
    private val importId: Long
) : IJob {
    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = ApplicantsImportStartCondition()

    override fun prepare(import: Import) {
        import.importProgress.importStatus = ImportStatus.STARTED
        import.importProgress.importedApplications = 0
    }

    override fun doWork() {
        val import = importRepository.getById(importId)
        import.importProgress.error = null
        val applicantDataSource = applicationDataSourceFactory.getDataSource(import.dataSourceId)
        var currentPage = 1
        try {
            var page = applicantDataSource.getApplicationsPage(
                programmeForeignId = import.programmeForeignId,
                registrationCode = import.registration,
                pageNumber = currentPage,
                import = import
            )
            import.importProgress.totalCount = page.getSize()
            importProgressRepository.save(import.importProgress)
            if (page.getSize() == 0) {
                throw IllegalStateException("Liczba kandydatów wynosi 0!")
            }
            page.getResultsList().forEach {
                processService.importApplication(importId, it, applicantDataSource)
            }
            currentPage++
            while (page.hasNext()) {
                page = applicantDataSource.getApplicationsPage(
                    programmeForeignId = import.programmeForeignId,
                    registrationCode = import.registration,
                    pageNumber = currentPage,
                    import = import
                )
                page.getResultsList().forEach {
                    processService.importApplication(importId, it, applicantDataSource)
                }
                currentPage++
            }
            importService.setImportStatus(ImportStatus.IMPORTED, importId)
        } catch (e: Exception) {
            throw ImportException(import.id!!, e.message, e)
        }
    }
}