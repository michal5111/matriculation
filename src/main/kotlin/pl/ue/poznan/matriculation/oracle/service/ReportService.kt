package pl.ue.poznan.matriculation.oracle.service

import org.springframework.stereotype.Service
import pl.ue.poznan.matriculation.local.repo.ApplicationRepository

@Service
class ReportService(
       private val applicationRepository: ApplicationRepository
) {

}