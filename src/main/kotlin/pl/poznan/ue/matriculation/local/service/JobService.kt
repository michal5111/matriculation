package pl.poznan.ue.matriculation.local.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.exception.ImportNotFoundException
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.job.JobFactory
import pl.poznan.ue.matriculation.local.job.JobType
import pl.poznan.ue.matriculation.local.repo.ImportRepository

@Service
class JobService(
    private val asyncService: AsyncService,
    private val importRepository: ImportRepository,
    private val jobFactory: JobFactory
) {

    private fun getImport(importId: Long): Import {
        return importRepository.findByIdOrNull(importId) ?: throw ImportNotFoundException("Nie znaleziono importu.")
    }

    fun runJob(jobType: JobType, importId: Long) {
        val import = getImport(importId)
        val job = jobFactory.create(jobType, importId)
        job.startCondition.canStart(import)
        job.prepare(import)
        importRepository.save(import)
        asyncService.doWorkAsync(job)
    }
}