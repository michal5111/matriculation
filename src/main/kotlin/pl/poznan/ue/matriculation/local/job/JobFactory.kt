package pl.poznan.ue.matriculation.local.job

import org.springframework.stereotype.Component
import pl.poznan.ue.matriculation.local.job.JobType.*
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.local.service.ProcessService

@Component
class JobFactory(
    private val importService: ImportService,
    private val processService: ProcessService,
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
) {
    fun create(jobType: JobType, importId: Long): IJob {
        return when (jobType) {
            IMPORT -> ImportApplicantsJob(
                importService = importService,
                processService = processService,
                applicationDataSourceFactory = applicationDataSourceFactory,
                importId = importId
            )
            SAVE -> SavePersonsJob(
                processService = processService,
                importId = importId,
                applicationDataSourceFactory = applicationDataSourceFactory
            )
            FIND_UIDS -> GetUidsJob(
                processService = processService,
                importId = importId
            )
            ARCHIVE -> ArchiveApplicationsJob(
                processService = processService,
                importId = importId
            )
            SEND_NOTIFICATIONS -> SendNotificationsJob(
                processService = processService,
                applicationDataSourceFactory = applicationDataSourceFactory,
                importId = importId
            )
            CHECK_FOR_POTENTIAL_DUPLICATES -> CheckForPotentialDuplicatesJob(
                processService = processService,
                importId = importId
            )
        }
    }
}
