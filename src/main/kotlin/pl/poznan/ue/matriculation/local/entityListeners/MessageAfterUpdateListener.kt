@file:Suppress("JpaEntityListenerInspection")

package pl.poznan.ue.matriculation.local.entityListeners

import jakarta.persistence.PostPersist
import jakarta.persistence.PostRemove
import jakarta.persistence.PostUpdate
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import pl.poznan.ue.matriculation.kotlinExtensions.toDto
import pl.poznan.ue.matriculation.local.domain.import.Import

@Component
class MessageAfterUpdateListener(
    private val simpMessagingTemplate: SimpMessagingTemplate
) {

    @PostUpdate
    fun afterUpdate(entity: Import) {
        simpMessagingTemplate.convertAndSend("/topic/import/${entity.id}", entity.toDto())
        simpMessagingTemplate.convertAndSend("/topic/import", entity.toDto())
    }

    @PostRemove
    fun afterDelete(entity: Import) {
        simpMessagingTemplate.convertAndSend("/topic/delete/import", entity.toDto())
    }

    @PostPersist
    fun afterInsert(entity: Import) {
        simpMessagingTemplate.convertAndSend("/topic/insert/import", entity.toDto())
    }
}
