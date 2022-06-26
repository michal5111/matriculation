package pl.poznan.ue.matriculation.local.service

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.local.job.IJob
import pl.poznan.ue.matriculation.local.job.JobType
import pl.poznan.ue.matriculation.local.job.startConditions.StateTransitionFailure
import java.util.*

@Service
class JobService(
    private val asyncService: AsyncService,
    private val importService: ImportService,
    jobs: List<IJob>
) {

    private val jobsMap: MutableMap<JobType, IJob> = EnumMap(JobType::class.java)

    init {
        jobs.forEach {
            jobsMap[it.jobType] = it
        }
    }

    fun runJob(jobType: JobType, importId: Long) {
        try {
            var import = importService.get(importId)
            if (!UserService.checkDataSourcePermission(import.dataSourceId)) {
                throw ResponseStatusException(HttpStatus.FORBIDDEN)
            }
            val job = jobsMap[jobType] ?: throw IllegalArgumentException("Unknown job")
            when (val stateTransitionResult = job.startCondition.canStart(import)) {
                is StateTransitionFailure -> throw IllegalStateException(stateTransitionResult.message)
                else -> {}
            }
            import.importStatus = job.getInProgressStatus()
            job.prepare(import)
            import.error = null
            import.stackTrace = null
            import = importService.save(import)
            asyncService.doWorkAsync(job, import)
        } catch (e: Exception) {
            throw ImportException(importId, "Error running job: ${e.message}", e)
        }
    }
}
