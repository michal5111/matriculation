package pl.ue.poznan.matriculation.local.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.ue.poznan.matriculation.local.domain.applications.Application

@Repository
interface ApplicationRepository: JpaRepository<Application, Long> {
}