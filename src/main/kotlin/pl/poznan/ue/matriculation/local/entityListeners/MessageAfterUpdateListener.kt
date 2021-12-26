@file:Suppress("JpaEntityListenerInspection")

package pl.poznan.ue.matriculation.local.entityListeners

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import pl.poznan.ue.matriculation.local.domain.import.Import
import javax.persistence.PreUpdate

@Component
class MessageAfterUpdateListener(
    private val simpMessagingTemplate: SimpMessagingTemplate
) {

    @PreUpdate
    fun beforeUpdate(entity: Import) {
        simpMessagingTemplate.convertAndSend("/topic/importProgress/${entity.id}", entity)
    }
}
