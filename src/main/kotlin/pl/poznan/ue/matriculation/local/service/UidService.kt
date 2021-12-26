package pl.poznan.ue.matriculation.local.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.exception.ImportNotFoundException
import pl.poznan.ue.matriculation.ldap.repo.LdapUserRepository
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.repo.ApplicantRepository
import pl.poznan.ue.matriculation.local.repo.ImportRepository

@Service
class UidService(
    private val importRepository: ImportRepository,
    private val ldapUserRepository: LdapUserRepository,
    private val applicantRepository: ApplicantRepository
) {

    val logger: Logger = LoggerFactory.getLogger(UidService::class.java)

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRES_NEW,
        transactionManager = "transactionManager"
    )
    fun get(applicant: Applicant, importId: Long) {
        try {
            val importProgress = importRepository.findByIdOrNull(importId) ?: throw ImportNotFoundException()
            logger.info("Searching for ldap user for usosId {}", applicant.usosId)
            val ldapUser = applicant.usosId?.let {
                ldapUserRepository.findByUsosId(it)
            } ?: return
            logger.info("Found ldap user {} for usosId {}", ldapUser.uid, applicant.usosId)
            applicant.uid = ldapUser.uid
            logger.info("Apllicant uid is {}", applicant.uid)
            applicantRepository.save(applicant)
            importProgress.importedUids++
        } catch (e: Exception) {
            throw ImportException(importId, "Błąd przy pobieraniu uidów", e)
        }
    }
}
