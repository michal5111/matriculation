package pl.poznan.ue.matriculation.local.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.annotation.LogExecutionTime
import pl.poznan.ue.matriculation.applicantDataSources.IApplicationDataSource
import pl.poznan.ue.matriculation.applicantDataSources.IPhotoDownloader
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.exception.ApplicationNotFoundException
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.dto.IApplicantDto
import pl.poznan.ue.matriculation.local.dto.IApplicationDto
import pl.poznan.ue.matriculation.local.processor.ProcessRequest
import pl.poznan.ue.matriculation.local.processor.TargetSystemProcessor
import pl.poznan.ue.matriculation.oracle.domain.Person

@Component
class ApplicationProcessor(
    private val applicantService: ApplicantService,
    private val importService: ImportService,
    private val asyncService: AsyncService,
    private val targetSystemProcessor: TargetSystemProcessor<Person>,
    private val applicationService: ApplicationService
) {

    val logger: Logger = LoggerFactory.getLogger(ApplicationProcessor::class.java)

    @LogExecutionTime
    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRES_NEW,
        transactionManager = "transactionManager",
    )
    fun processApplication(
        importId: Long,
        applicationId: Long,
        applicationDtoDataSource: IApplicationDataSource<IApplicationDto, IApplicantDto>
    ) {
        logger.trace("------------------------------------------------Przetwarzam ${applicationId}---------------------------------------------")
        val application =
            applicationService.findWithApplicantById(applicationId) ?: throw ApplicationNotFoundException()
        val applicant = application.applicant ?: throw ApplicantNotFoundException()
        logger.trace("Sprawdzam aplikanta")
        applicantService.check(applicant)
        logger.trace("Pobieram progres importu")
        val import = importService.findById(importId)
        logger.trace("Sprawdzam czy źródło danych implementuje pobieranie zdjęć")
        if (applicationDtoDataSource is IPhotoDownloader) {
            applicant.photo?.let {
                logger.trace("Pobieram zdjęcie")
                applicant.photoByteArrayFuture = asyncService.doAsync {
                    applicationDtoDataSource.getPhoto(it)
                }
            }
        }
        logger.trace("Przetwarzam pobranego aplikanta")
        val processRequest = ProcessRequest(
            application = application,
            import = import,
            person = null
        )
        val processResult = targetSystemProcessor.process(processRequest)
        logger.trace("Przypisuje aplikantowi nadany numer indeksu i usosId")
        application.apply {
            applicant.usosId = processResult.systemId
            applicant.assignedIndexNumber = processResult.assignedIndexNumber
            importError = null
            stackTrace = null
            importStatus = ApplicationImportStatus.IMPORTED
        }
        logger.trace("Zapisuję aplikanta")
        applicantService.save(applicant)
        logger.trace("Zapisuję zgłoszenie")
        applicationService.save(application)
        import.savedApplicants++
        logger.trace("------------------------------------------------koniec ${application.id}---------------------------------------------")
    }
}
