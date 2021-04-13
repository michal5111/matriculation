package pl.poznan.ue.matriculation.exception.exceptionHandler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pl.poznan.ue.matriculation.exception.ImportException
import pl.poznan.ue.matriculation.exception.ImportStartException
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
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
                val importId = e.importId
                if (e.cause is SQLException) {
                    importService.setError(
                        importId, (e.cause as SQLException).sqlState + "\n" + e.message.toString()
                    )
                } else {
                    importService.setError(importId, e.message.toString())
                }
                importService.setImportStatus(ImportStatus.ERROR, importId)
            }
            if (e is ImportStartException) {
                val importId = e.importId
                importService.setError(importId, e.message.toString())
                importService.setImportStatus(ImportStatus.ERROR, importId)
            }
            e = e?.cause
        } while (e != null)
        logger.error("Exception message - " + throwable.message)
        logger.error("Cause " + throwable.cause?.message)
        logger.error("Method name - " + method.name)
        for (param in objects) {
            logger.error("Parameter value - $param")
        }
        logger.error("Async exception", throwable)
    }
}