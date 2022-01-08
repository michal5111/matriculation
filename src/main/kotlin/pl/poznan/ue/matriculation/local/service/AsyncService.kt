package pl.poznan.ue.matriculation.local.service

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
    @Async("defaultTaskExecutor")
    fun doWorkAsync(job: IJob, import: Import) {
        job.status = JobStatus.WORKING
        try {
            val import2 = job.doWork(import)
            val importId: Long = import.id ?: throw IllegalArgumentException("Import id is null")
            importService.setImportStatus(importId = importId, importStatus = job.getCompletionStatus(import2))
        } catch (e: Exception) {
            job.status = JobStatus.ERROR
            throw ImportException(import.id, "Import job error", e)
        }
        job.status = JobStatus.DONE
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
