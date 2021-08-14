package pl.poznan.ue.matriculation.exception.exceptionHandler

import org.hibernate.JDBCException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.kotlinExtensions.stackTraceToString
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.repo.ApplicantRepository
import pl.poznan.ue.matriculation.local.repo.ApplicationRepository
import pl.poznan.ue.matriculation.local.repo.ImportProgressRepository
import java.lang.reflect.UndeclaredThrowableException

@Component
class SaveExceptionHandler(
    private val importProgressRepository: ImportProgressRepository,
    private val applicantRepository: ApplicantRepository,
    private val applicationRepository: ApplicationRepository
) : ISaveExceptionHandler {

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRES_NEW,
        transactionManager = "transactionManager"
    )
    override fun handle(exception: Exception, application: Application, importId: Long) {
        val importProgress = importProgressRepository.getById(importId)
        application.importError = ""
        var e: Throwable? = exception
        do {
            if (e is UndeclaredThrowableException) {
                e = e.cause
            }
            if (e is JDBCException) {
                application.importError += "${e.javaClass.simpleName}: ${e.message} Error code: ${e.errorCode} " +
                        "Sql: ${e.sql} " +
                        "Sql state: ${e.sqlState} "
            } else {
                application.importError += "${e?.javaClass?.simpleName}: ${e?.message} "
            }
            e = e?.cause
        } while (e != null)
        application.importStatus = ApplicationImportStatus.ERROR
        application.stackTrace = exception.stackTraceToString()
        importProgress.saveErrors++
        applicantRepository.save(application.applicant!!)
        applicationRepository.save(application)
    }
}