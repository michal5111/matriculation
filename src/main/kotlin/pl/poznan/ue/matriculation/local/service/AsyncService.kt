package pl.poznan.ue.matriculation.local.service

import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.local.job.IJob
import pl.poznan.ue.matriculation.local.job.JobStatus
import java.util.concurrent.Future

@Service
class AsyncService {
    @Async("defaultTaskExecutor")
    fun doWorkAsync(job: IJob) {
        job.status = JobStatus.WORKING
        try {
            job.doWork()
        } catch (e: Exception) {
            job.status = JobStatus.ERROR
            throw e
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
