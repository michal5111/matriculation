package pl.poznan.ue.matriculation.configuration

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import pl.poznan.ue.matriculation.exception.exceptionHandler.AsyncExceptionHandler
import java.util.concurrent.Executor


@Configuration
@EnableAsync
class AsyncConfiguration() : AsyncConfigurer {

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler? {
        return AsyncExceptionHandler()
    }

    @Bean
    fun taskExecutor(): Executor? {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 1
        executor.maxPoolSize = 1
        executor.setQueueCapacity(50)
        executor.setThreadNamePrefix("USOS-WORKER-")
        executor.initialize()
        return executor
    }
}