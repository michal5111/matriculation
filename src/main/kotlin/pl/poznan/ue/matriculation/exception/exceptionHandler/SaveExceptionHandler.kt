package pl.poznan.ue.matriculation.exception.exceptionHandler

import org.hibernate.JDBCException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.kotlinExtensions.stackTraceToHtmlString
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.repo.ApplicationRepository
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import java.lang.reflect.UndeclaredThrowableException

@Component
class SaveExceptionHandler(
    private val importRepository: ImportRepository,
    private val applicationRepository: ApplicationRepository
) : ISaveExceptionHandler {

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRES_NEW,
        transactionManager = "transactionManager"
    )
    override fun handle(exception: Exception, applicationId: Long, importId: Long) {
        val import = importRepository.findByIdOrNull(importId) ?: error("Import not found")
        val application = applicationRepository.findByIdOrNull(applicationId) ?: throw ApplicantNotFoundException()
        application.importError = ""
        var e: Throwable? = exception
        do {
            when (e) {
                is UndeclaredThrowableException -> e = e.cause
                is JDBCException -> {
                    application.importError += "${e.javaClass.simpleName}: ${e.message.orEmpty()} Error code: ${e.errorCode} " +
                        "Sql: ${e.sql} " +
                        "Sql state: ${e.sqlState} "
                }

                else -> application.importError += "${e?.javaClass?.simpleName}: ${e?.message.orEmpty()}"
            }
            e = e?.cause
        } while (e != null)
        application.importStatus = ApplicationImportStatus.ERROR
        application.stackTrace = exception.stackTraceToHtmlString()
        import.saveErrors++
    }
}
