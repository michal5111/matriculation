package pl.poznan.ue.matriculation.local.service

import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.local.job.JobFactory
import pl.poznan.ue.matriculation.local.job.JobType

@Service
class JobService(
    private val asyncService: AsyncService,
    private val importService: ImportService,
    private val jobFactory: JobFactory
) {

    fun runJob(jobType: JobType, importId: Long) {
        try {
            var import = importService.get(importId)
            val job = jobFactory.create(jobType, importId)
            job.startCondition.canStart(import)
            import.importStatus = job.getInProgressStatus()
            job.prepare(import)
            import.error = null
            import = importService.save(import)
            asyncService.doWorkAsync(job, import)
        } catch (e: Exception) {
            throw ImportException(importId, "Error running job", e)
        }
    }
}
