package pl.poznan.ue.matriculation.local.job

import pl.poznan.ue.matriculation.applicantDataSources.INotificationSender
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.SendNotificationsStartConditions
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.local.service.ProcessService

class SendNotificationsJob(
    private val processService: ProcessService,
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
    private val importId: Long,
    private val importService: ImportService
) : IJob {
    override var status: JobStatus = JobStatus.PENDING
    override val startCondition: IStartConditions
        get() = SendNotificationsStartConditions()

    override fun prepare(import: Import) {
        import.notificationsSend = 0
        import.importStatus = ImportStatus.SENDING_NOTIFICATIONS
    }

    override fun doWork() {
        val import = importService.get(importId)
        val ads = applicationDataSourceFactory.getDataSource(import.dataSourceId)
        if (ads !is INotificationSender) return
        processService.sendNotifications(importId = importId, ads)
        if (import.saveErrors > 0) {
            importService.get(importId).apply {
                importStatus = ImportStatus.COMPLETED_WITH_ERRORS
            }.let {
                importService.save(it)
            }
        } else {
            importService.get(importId).apply {
                importStatus = ImportStatus.COMPLETE
            }.let {
                importService.save(it)
            }
        }
    }
}
