package pl.poznan.ue.matriculation.local.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.exception.ImportNotFoundException
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.enum.DuplicateStatus
import pl.poznan.ue.matriculation.local.repo.ApplicantRepository
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import pl.poznan.ue.matriculation.oracle.service.PersonService

@Component
class PotentialDuplicateFinder(
    private val importRepository: ImportRepository,
    private val personService: PersonService,
    private val applicantRepository: ApplicantRepository
) {

    val logger: Logger = LoggerFactory.getLogger(PotentialDuplicateFinder::class.java)

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRES_NEW,
        transactionManager = "transactionManager"
    )
    fun findPotentialDuplicate(applicant: Applicant, importId: Long) {
        val importProgress = importRepository.findByIdOrNull(importId) ?: throw ImportNotFoundException()
        val dateOfBirth = applicant.dateOfBirth ?: throw IllegalArgumentException("Date of birth is null")
        val potentialDuplicatesList = personService.findPotentialDuplicate(
            name = applicant.given,
            surname = applicant.family,
            birthDate = dateOfBirth,
            idNumbers = applicant.identityDocuments.map {
                it.number ?: throw IllegalArgumentException("Identity document number is null")
            },
            email = applicant.email,
            privateEmail = applicant.email
        )
        if (potentialDuplicatesList.isNotEmpty()) {
            logger.warn("Wykryto potencjalny duplikat!")
            applicant.potentialDuplicateStatus = DuplicateStatus.POTENTIAL_DUPLICATE
            importProgress.potentialDuplicates++
        } else {
            applicant.potentialDuplicateStatus = DuplicateStatus.OK
        }
        applicantRepository.save(applicant)
    }
}
