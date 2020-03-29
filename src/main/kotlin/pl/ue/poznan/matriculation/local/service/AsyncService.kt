package pl.ue.poznan.matriculation.local.service

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import pl.ue.poznan.matriculation.local.domain.import.Import
import pl.ue.poznan.matriculation.local.service.ImportService

@Service
class AsyncService(
        private val importService: ImportService
) {

    @Async
    fun importApplicantsAsync(import: Import) {
        importService.importApplications(import)
    }

    @Async
    fun savePersons(import: Import) {
        importService.savePersons(import)
    }
}