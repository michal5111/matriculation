package pl.poznan.ue.matriculation.local.service

import org.springframework.core.io.ClassPathResource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.applicantDataSources.INotificationSender
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.exception.ImportNotFoundException
import pl.poznan.ue.matriculation.irk.dto.NotificationDto
import pl.poznan.ue.matriculation.local.domain.applications.Application
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
        application: Application,
        importId: Long,
        notificationSender: INotificationSender
    ) {
        val personExisted = application.applicant?.personExisted ?: false
        val importProgress = importRepository.findByIdOrNull(importId) ?: throw ImportNotFoundException()
        var headerPl = "Twoje konto USOS zostało utworzone."
        var headerEn = "Your USOS account has been created."
        val templatePl = if (personExisted) {
            headerPl = "Twoje konto USOS zostało aktywowane."
            ClassPathResource("notificationEmailTemplateExistingPl.txt").file
        } else {
            ClassPathResource("notificationEmailTemplatePl.txt").file
        }
        val templateEn = if (personExisted) {
            headerEn = "Your USOS account has been activated."
            ClassPathResource("notificationEmailTemplateExistingEn.txt").file
        } else {
            ClassPathResource("notificationEmailTemplate.txtEn").file
        }
        val applicant = application.applicant ?: throw ApplicantNotFoundException()

        val notificationDto = NotificationDto(
            headerPl = headerPl,
            headerEn = headerEn,
            messagePl = templatePl.readText().replace("{{NIU}}", applicant.uid.toString()),
            messageEn = templateEn.readText().replace("{{NIU}}", applicant.uid.toString())
        )
        notificationSender.sendNotification(applicant.foreignId, notificationDto)
        importProgress.notificationsSend++
        application.notificationSent = true
    }
}
