package pl.poznan.ue.matriculation.local.job

import org.springframework.stereotype.Component
import pl.poznan.ue.matriculation.local.job.JobType.*
import pl.poznan.ue.matriculation.local.repo.ImportProgressRepository
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import pl.poznan.ue.matriculation.local.service.ApplicationDataSourceFactory
import pl.poznan.ue.matriculation.local.service.ImportService
import pl.poznan.ue.matriculation.local.service.ProcessService

@Component
class JobFactory(
    private val importService: ImportService,
    private val importRepository: ImportRepository,
    private val processService: ProcessService,
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
    private val importProgressRepository: ImportProgressRepository
) {
    fun create(jobType: JobType, importId: Long): IJob {
        return when (jobType) {
            IMPORT -> ImportApplicantsJob(
                applicationDataSourceFactory = applicationDataSourceFactory,
                importRepository = importRepository,
                importProgressRepository = importProgressRepository,
                processService = processService,
                importService = importService,
                importId = importId
            )
            SAVE -> SavePersonsJob(
                applicationDataSourceFactory = applicationDataSourceFactory,
                importId = importId,
                importRepository = importRepository,
                importService = importService,
                processService = processService
            )
            FIND_UIDS -> GetUidsJob(
                processService = processService,
                importService = importService,
                importRepository = importRepository,
                importId = importId
            )
            ARCHIVE -> ArchiveApplicationsJob(
                processService = processService,
                importId = importId
            )
            SEND_NOTIFICATIONS -> SendNotificationsJob(
                processService = processService,
                applicationDataSourceFactory = applicationDataSourceFactory,
                importId = importId,
                importRepository = importRepository,
                importService = importService
            )
        }
    }
}