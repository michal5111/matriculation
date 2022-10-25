package pl.poznan.ue.matriculation.exception.exceptionHandler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.kotlinExtensions.stackTraceToHtmlString
import pl.poznan.ue.matriculation.local.service.ImportService
import java.lang.reflect.Method
import java.sql.SQLException

@Component
class AsyncExceptionHandler : AsyncUncaughtExceptionHandler {

    @Autowired
    private lateinit var importService: ImportService

    val logger: Logger = LoggerFactory.getLogger(AsyncExceptionHandler::class.java)

    override fun handleUncaughtException(throwable: Throwable, method: Method, vararg objects: Any?) {
        var e: Throwable? = throwable
        do {
            if (e is ImportException) {
                handleImportException(e)
            }
            e = e?.cause
        } while (e != null)
        logger.error(
            """
            Exception message: ${throwable.message}
            Cause: ${throwable.cause?.message}
            Method name: ${method.name}
            Params: ${objects.joinToString { it.toString() }}
        """.trimIndent(), throwable
        )
    }

    private fun handleImportException(e: ImportException) {
        val importId = e.importId ?: return
        when (val cause = e.cause) {
            is SQLException -> {
                importService.setError(
                    importId,
                    "${cause.sqlState.orEmpty()} ${e.message ?: "Unknown error"}",
                    e.stackTraceToHtmlString()
                )
            }

            else -> importService.setError(
                importId,
                e.message ?: "Unknown error",
                e.stackTraceToHtmlString()
            )
        }
    }


}
