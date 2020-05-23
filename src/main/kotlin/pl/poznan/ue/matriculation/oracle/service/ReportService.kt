package pl.poznan.ue.matriculation.oracle.service

import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.local.repo.ApplicationRepository

@Service
class ReportService(
       private val applicationRepository: ApplicationRepository
) {

}