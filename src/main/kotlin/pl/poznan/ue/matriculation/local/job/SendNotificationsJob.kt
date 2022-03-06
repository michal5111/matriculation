package pl.poznan.ue.matriculation.local.job

import pl.poznan.ue.matriculation.applicantDataSources.INotificationSender
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.SendNotificationsStartConditions
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.local.service.ProcessService

class SendNotificationsJob(
    private val processService: ProcessService,
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
    private val importId: Long
) : IJob {
    override var status: JobStatus = JobStatus.PENDING
    override val startCondition: IStartConditions
        get() = SendNotificationsStartConditions()

    override fun prepare(import: Import) {
        import.notificationsSend = 0
    }

    override fun doWork(import: Import) {
        val ads = applicationDataSourceFactory.getDataSource(import.dataSourceId)
        if (ads is INotificationSender) {
            processService.sendNotifications(importId = importId, ads)
        }
    }

    override fun getCompletionStatus(import: Import): ImportStatus {
        return if (import.saveErrors > 0) {
            ImportStatus.COMPLETED_WITH_ERRORS
        } else {
            ImportStatus.COMPLETE
        }
    }

    override fun getInProgressStatus(): ImportStatus {
        return ImportStatus.SENDING_NOTIFICATIONS
    }
}
