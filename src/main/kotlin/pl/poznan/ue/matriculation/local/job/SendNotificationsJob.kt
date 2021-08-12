package pl.poznan.ue.matriculation.local.job

import pl.poznan.ue.matriculation.applicantDataSources.INotificationSender
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.SendNotificationsStartConditions
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.local.service.ProcessService

class SendNotificationsJob(
    private val processService: ProcessService,
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
    private val importId: Long,
    private val importRepository: ImportRepository,
    private val importService: ImportService
) : IJob {
    override var status: JobStatus = JobStatus.PENDING
    override val startCondition: IStartConditions
        get() = SendNotificationsStartConditions()

    override fun prepare(import: Import) {
        import.importProgress.notificationsSend = 0
        import.importProgress.importStatus = ImportStatus.SENDING_NOTIFICATIONS
    }

    override fun doWork() {
        val import = importRepository.getOne(importId)
        val ads = applicationDataSourceFactory.getDataSource(import.dataSourceId)
        if (ads !is INotificationSender) return
        processService.sendNotifications(importId = importId, ads)
        if (import.importProgress.saveErrors > 0) {
            importService.setImportStatus(ImportStatus.COMPLETED_WITH_ERRORS, importId)
        } else {
            importService.setImportStatus(ImportStatus.COMPLETE, importId)
        }
    }
}