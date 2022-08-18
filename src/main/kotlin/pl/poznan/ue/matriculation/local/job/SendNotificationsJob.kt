package pl.poznan.ue.matriculation.local.job

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.applicantDataSources.INotificationSender
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.job.startConditions.SendNotificationsStartConditions
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.local.service.ApplicationService
import pl.poznan.ue.matriculation.local.service.NotificationService

@Component
class SendNotificationsJob(
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
    private val applicationService: ApplicationService,
    private val notificationService: NotificationService
) : IJob {
    override val jobType: JobType = JobType.SEND_NOTIFICATIONS

    override var status: JobStatus = JobStatus.PENDING
    override val startCondition: IStartConditions
        get() = SendNotificationsStartConditions()

    override fun prepare(import: Import) {
        import.notificationsSend = 0
    }

    @Transactional(propagation = Propagation.REQUIRED, transactionManager = "transactionManager")
    override fun doWork(import: Import) {
        val importId = import.id ?: throw IllegalArgumentException("Import id is null")
        val ads = applicationDataSourceFactory.getDataSource(import.dataSourceId)
        if (ads is INotificationSender) {
            val applicationStream = applicationService.findAllByImportIdAndNotificationSent(importId, false)
            applicationStream.use {
                it.forEach { application ->
                    notificationService.sendNotification(application, importId, ads)
                }
            }
        }
    }

    override fun getCompletionStatus(import: Import): ImportStatus {
        return if (import.saveErrors > 0) {
            ImportStatus.COMPLETED_WITH_ERRORS
        } else {
            ImportStatus.COMPLETE
        }
    }

    override fun getInProgressStatus(): ImportStatus = ImportStatus.SENDING_NOTIFICATIONS
}
