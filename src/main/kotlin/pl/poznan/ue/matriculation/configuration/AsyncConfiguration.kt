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
class AsyncConfiguration(private val asyncExceptionHandler: AsyncExceptionHandler) : AsyncConfigurer {

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler? {
        return asyncExceptionHandler
    }

    @Bean(name = ["defaultTaskExecutor"])
    fun taskExecutor(): Executor? {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 1
        executor.maxPoolSize = 1
        executor.queueCapacity = 50
        executor.setThreadNamePrefix("ASYNC-WORKER-")
        executor.initialize()
        return executor
    }

    @Bean(name = ["processTaskExecutor"])
    fun processTaskExecutor(): Executor? {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 5
        executor.maxPoolSize = 30
        executor.queueCapacity = 100
        executor.setThreadNamePrefix("PROCESS-ASYNC-WORKER-")
        executor.initialize()
        return executor
    }
}
