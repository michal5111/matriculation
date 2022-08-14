//package pl.poznan.ue.matriculation.configuration
//
//import org.springframework.context.annotation.Configuration
//import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry
//import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer
//
//@Configuration
//class SocketSecurityConfig : AbstractSecurityWebSocketMessageBrokerConfigurer() {
//    override fun configureInbound(messages: MessageSecurityMetadataSourceRegistry) {
//        messages
//            .simpSubscribeDestMatchers("/import/**").authenticated()
//    }
//}
