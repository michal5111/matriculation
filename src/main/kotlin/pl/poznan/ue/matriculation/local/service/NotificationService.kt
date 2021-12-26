package pl.poznan.ue.matriculation.local.service

import org.springframework.core.io.ClassPathResource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.applicantDataSources.INotificationSender
import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.exception.ImportNotFoundException
import pl.poznan.ue.matriculation.irk.dto.NotificationDto
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.repo.ImportRepository

@Service
class NotificationService(
    private val importRepository: ImportRepository
) {
    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRES_NEW,
        transactionManager = "transactionManager"
    )
    fun sendNotification(
        applicant: Applicant,
        importId: Long,
        notificationSender: INotificationSender
    ) {
        try {
            val importProgress = importRepository.findByIdOrNull(importId) ?: throw ImportNotFoundException()
            val template = ClassPathResource("notificationEmailTemplate.txt").file

            val notificationDto = NotificationDto(
                header = "Twoje konto USOS zostało utworzone. / Your USOS account has been created.",
                message = template.readText().replace("{{NIU}}", applicant.uid.toString())
            )
            notificationSender.sendNotification(applicant.foreignId, notificationDto)
            importProgress.notificationsSend++
        } catch (e: Exception) {
            throw ImportException(importId, "Błąd przy wysyłaniu powiadomień", e)
        }
    }
}
