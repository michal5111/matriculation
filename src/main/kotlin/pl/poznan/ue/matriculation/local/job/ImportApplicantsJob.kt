package pl.poznan.ue.matriculation.local.job

import org.springframework.stereotype.Component
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.ApplicantsImportStartCondition
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.local.service.ProcessService

@Component
class ImportApplicantsJob(
    private val importService: ImportService,
    private val processService: ProcessService,
    private val applicationDataSourceFactory: ApplicationDataSourceFactory
) : IJob {
    override val jobType: JobType = JobType.IMPORT

    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = ApplicantsImportStartCondition()

    override fun prepare(import: Import) {
        import.importedApplications = 0
    }

    override fun doWork(import: Import) {
        val importId = import.id ?: throw IllegalArgumentException("Import id is null")
        val applicantDataSource = applicationDataSourceFactory.getDataSource(import.dataSourceId)
        var currentPage = 1
        var page = applicantDataSource.getApplicationsPage(
            programmeForeignId = import.programmeForeignId,
            registrationCode = import.registration,
            pageNumber = currentPage,
            import = import
        )
        import.totalCount = page.getTotalSize()
        importService.save(import)
        if (page.getTotalSize() == 0) {
            throw IllegalStateException("Liczba kandydatów wynosi 0!")
        }
        page.getContent().forEach {
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
            page.getContent().forEach {
                processService.importApplication(importId, it, applicantDataSource)
            }
            currentPage++
        }
    }

    override fun getCompletionStatus(import: Import): ImportStatus {
        return ImportStatus.IMPORTED
    }

    override fun getInProgressStatus(): ImportStatus {
        return ImportStatus.STARTED
    }
}
