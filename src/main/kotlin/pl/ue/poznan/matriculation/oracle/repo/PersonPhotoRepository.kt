package pl.ue.poznan.matriculation.oracle.repo

import org.springframework.data.jpa.repository.JpaRepository
import pl.ue.poznan.matriculation.oracle.domain.PersonPhoto

interface PersonPhotoRepository: JpaRepository<PersonPhoto, Long>