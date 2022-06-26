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
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.IApplicantDto
import pl.poznan.ue.matriculation.local.dto.IApplicationDto
import pl.poznan.ue.matriculation.local.repo.ImportRepository
import pl.poznan.ue.matriculation.oracle.service.PersonService

@Component
class ApplicationProcessor(
    private val applicantService: ApplicantService,
    private val importRepository: ImportRepository,
    private val asyncService: AsyncService,
    private val personService: PersonService,
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
        application: Application,
        importDto: Import,
        applicationDtoDataSource: IApplicationDataSource<IApplicationDto, IApplicantDto>
    ) {
        logger.trace("------------------------------------------------Przetwarzam ${application.id}---------------------------------------------")
        val applicant = application.applicant ?: throw ApplicantNotFoundException()
        logger.trace("Sprawdzam aplikanta")
        applicantService.check(applicant)
        logger.trace("Pobieram progres importu")
        val importProgress = importRepository.getById(importId)
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
        val personAndStudent = personService.process(
            application = application,
            importDto = importDto,
            postMatriculation = applicationDtoDataSource::postMatriculation
        )
        logger.trace("Przypisuje aplikantowi nadany numer indeksu i usosId")
        application.apply {
            applicant.usosId = personAndStudent.first.id
            applicant.assignedIndexNumber = personAndStudent.second.indexNumber
            importError = null
            stackTrace = null
            importStatus = ApplicationImportStatus.IMPORTED
        }
        logger.trace("Zapisuję aplikanta")
        applicantService.save(applicant)
        logger.trace("Zapisuję zgłoszenie")
        applicationService.save(application)
        importProgress.savedApplicants++
        logger.trace("------------------------------------------------koniec ${application.id}---------------------------------------------")
    }
}
