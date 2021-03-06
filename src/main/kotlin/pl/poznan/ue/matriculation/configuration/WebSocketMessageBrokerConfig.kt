package pl.poznan.ue.matriculation.configuration


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy
import org.springframework.web.socket.server.support.DefaultHandshakeHandler

@Configuration
@EnableWebSocketMessageBroker
class WebSocketMessageBrokerConfig : WebSocketMessageBrokerConfigurer {

    val logger: Logger = LoggerFactory.getLogger(WebSocketMessageBrokerConfig::class.java)

    @Value("\${pl.poznan.ue.matriculation.service.home}")
    private lateinit var appServiceHome: String

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws")
            .setAllowedOrigins(appServiceHome)
            .setHandshakeHandler(DefaultHandshakeHandler(TomcatRequestUpgradeStrategy()))
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.setApplicationDestinationPrefixes("/app")
        registry.enableSimpleBroker("/topic", "/queue")
    }
}
