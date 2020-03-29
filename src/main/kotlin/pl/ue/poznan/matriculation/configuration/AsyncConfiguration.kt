package pl.ue.poznan.matriculation.configuration

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import pl.ue.poznan.matriculation.exception.exceptionHandler.AsyncExceptionHandler
import java.util.concurrent.Executor


@Configuration
@EnableAsync
class AsyncConfiguration: AsyncConfigurer {

    @Autowired
    lateinit var asyncExceptionHandler: AsyncExceptionHandler

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler? {
        return asyncExceptionHandler
    }

    @Bean
    fun asyncExceptionHandlerBean(): AsyncExceptionHandler {
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