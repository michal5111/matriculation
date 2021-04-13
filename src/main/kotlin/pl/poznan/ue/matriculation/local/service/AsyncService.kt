package pl.poznan.ue.matriculation.local.service

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.local.job.IJob
import pl.poznan.ue.matriculation.local.job.JobStatus

@Service
class AsyncService {
    @Async
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
}