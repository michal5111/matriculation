package pl.poznan.ue.matriculation.configuration

import org.springframework.boot.actuate.trace.http.HttpTraceRepository
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
@Profile("dev")
class HttpTraceActuatorConfiguration {

    @Bean
    fun httpTraceRepository(): HttpTraceRepository? {
        return InMemoryHttpTraceRepository()
    }
}