@file:Suppress("JpaEntityListenerInspection")

package pl.poznan.ue.matriculation.local.entityListeners

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import pl.poznan.ue.matriculation.local.domain.import.Import
import javax.persistence.PrePersist
import javax.persistence.PreRemove
import javax.persistence.PreUpdate

@Component
class MessageAfterUpdateListener(
    private val simpMessagingTemplate: SimpMessagingTemplate
) {

    @PreUpdate
    fun beforeUpdate(entity: Import) {
        simpMessagingTemplate.convertAndSend("/topic/import/${entity.id}", entity)
        simpMessagingTemplate.convertAndSend("/topic/import", entity)
    }

    @PreRemove
    fun beforeDelete(entity: Import) {
        simpMessagingTemplate.convertAndSend("/topic/delete/import", entity)
    }

    @PrePersist
    fun beforeInsert(entity: Import) {
        simpMessagingTemplate.convertAndSend("/topic/insert/import", entity)
    }
}
