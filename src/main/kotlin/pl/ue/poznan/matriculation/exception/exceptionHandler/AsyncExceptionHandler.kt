package pl.ue.poznan.matriculation.exception.exceptionHandler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.beans.factory.annotation.Autowired
import pl.ue.poznan.matriculation.exception.ImportException
import pl.ue.poznan.matriculation.exception.ImportStartException
import pl.ue.poznan.matriculation.local.service.ImportService
import java.lang.reflect.Method
import java.sql.SQLException

class AsyncExceptionHandler: AsyncUncaughtExceptionHandler {

    @Autowired
    private lateinit var importService: ImportService

    val logger: Logger = LoggerFactory.getLogger(AsyncExceptionHandler::class.java)

    override fun handleUncaughtException(throwable: Throwable, method: Method, vararg objects: Any?) {
        if (throwable is ImportException) {
            val importId = throwable.importId
            if (throwable.cause is SQLException) {
                importService.setError(
                        importId, (throwable.cause as SQLException).sqlState+"\n"+
                        throwable.message.toString()
                                +"\n"+throwable.cause?.message
                                + throwable.cause?.stackTrace?.joinToString("\n", "\nStackTrace: ")
                )
            } else {
                importService.setError(
                        importId,
                        throwable.message.toString()
                                +"\n"+throwable.cause?.message
                                + throwable.cause?.stackTrace?.joinToString("\n", "\nStackTrace: ")
                )
            }
        }
        if (throwable is ImportStartException) {
            val importId = throwable.importId
            importService.setError(
                    importId,
                    throwable.message.toString()
                            +"\n"+throwable.cause?.message
                            + throwable.cause?.stackTrace?.joinToString("\n", "\nStackTrace: ")
            )
        }
        logger.error("Exception message - " + throwable.message)
        logger.error("Cause " + throwable.cause?.message)
        logger.error("Method name - " + method.name)
        for (param in objects) {
            logger.error("Parameter value - $param")
        }
        throwable.printStackTrace()
    }
}