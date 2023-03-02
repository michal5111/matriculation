package pl.poznan.ue.matriculation.local.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.IJob
import pl.poznan.ue.matriculation.local.job.JobStatus
import java.util.concurrent.Future

@Service
class AsyncService(
    private val importService: ImportService
) {
    private val logger = LoggerFactory.getLogger(AsyncService::class.java)

    @Async("defaultTaskExecutor")
    fun doWorkAsync(job: IJob, import: Import) {
        job.status = JobStatus.WORKING
        logger.info("Job started with status ${job.status} for import $import")
        try {
            job.doWork(import)
            val importId = import.id ?: throw IllegalArgumentException("Import id is null")
            val import2 = importService.findById(importId)
            import2.importStatus = job.getCompletionStatus(import2)
            importService.save(import2)
        } catch (e: Exception) {
            job.status = JobStatus.ERROR
            logger.error("Job completed with status ${job.status} for import $import", e)
            throw ImportException(import.id, e.message, e)
        }
        job.status = JobStatus.DONE
        logger.info("Job completed with status ${job.status} for import $import")
    }

    @Async("processTaskExecutor")
    fun <T> doAsync(work: () -> T?): Future<T>? {
        return try {
            AsyncResult(work.invoke())
        } catch (e: InterruptedException) {
            null
        }
    }
}
