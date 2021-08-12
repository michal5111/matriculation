package pl.poznan.ue.matriculation.applicantDataSources

import pl.poznan.ue.matriculation.irk.dto.NotificationDto

interface INotificationSender {
    fun sendNotification(foreignApplicantId: Long, notificationDto: NotificationDto)
}