package pl.poznan.ue.matriculation.local.job

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.annotation.LogExecutionTime
import pl.poznan.ue.matriculation.applicantDataSources.IApplicationDataSource
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.IApplicantDto
import pl.poznan.ue.matriculation.local.dto.IApplicationDto
import pl.poznan.ue.matriculation.local.job.startConditions.ApplicantsImportStartCondition
import pl.poznan.ue.matriculation.local.job.startConditions.IStartConditions
import pl.poznan.ue.matriculation.local.service.*

@Component
class ImportApplicantsJob(
    private val importService: ImportService,
    private val applicationDataSourceFactory: ApplicationDataSourceFactory,
    private val applicantService: ApplicantService,
    private val applicationService: ApplicationService,
    private val potentialDuplicateFinder: PotentialDuplicateFinder
) : IJob {
    override val jobType: JobType = JobType.IMPORT

    override var status: JobStatus = JobStatus.PENDING

    override val startCondition: IStartConditions
        get() = ApplicantsImportStartCondition()

    override fun prepare(import: Import) {
        import.importedApplications = 0
    }

    @Transactional(
        rollbackFor = [Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "transactionManager"
    )
    override fun doWork(import: Import) {
        val importId = import.id ?: throw IllegalArgumentException("Import id is null")
        val applicantDataSource = applicationDataSourceFactory.getDataSource(import.dataSourceId)
        var currentPage = 1
        var page = applicantDataSource.getApplicationsPage(
            programmeForeignId = import.programmeForeignId,
            registrationCode = import.registration,
            pageNumber = currentPage,
            import = import
        )
        import.totalCount = page.getTotalSize()
        importService.save(import)
        if (page.getTotalSize() == 0) {
            throw IllegalStateException("Liczba kandydatów wynosi 0!")
        }
        page.getContent().forEach {
            importApplication(importId, it, applicantDataSource)
        }
        currentPage++
        while (page.hasNext()) {
            page = applicantDataSource.getApplicationsPage(
                programmeForeignId = import.programmeForeignId,
                registrationCode = import.registration,
                pageNumber = currentPage,
                import = import
            )
            page.getContent().forEach {
                importApplication(importId, it, applicantDataSource)
            }
            currentPage++
        }
    }

    override fun getCompletionStatus(import: Import): ImportStatus = ImportStatus.IMPORTED

    override fun getInProgressStatus(): ImportStatus = ImportStatus.STARTED

    fun importApplication(
        importId: Long,
        applicationDto: IApplicationDto,
        applicationDtoDataSource: IApplicationDataSource<IApplicationDto, IApplicantDto>
    ) {
        val import = importService.get(importId)
        val applicantDto = applicationDtoDataSource.getApplicantById(applicationDto.foreignApplicantId, applicationDto)
        var application = createOrUpdateApplication(applicationDto, applicationDtoDataSource, import)
        if (application.import != null && application.import != import) {
            return
        }
        var applicant = createOrUpdateApplicant(applicantDto, applicationDtoDataSource, applicationDto)
        applicant.primaryIdentityDocument = applicationDtoDataSource.getPrimaryIdentityDocument(
            applicant = applicant,
            application = application,
            applicantDto = applicantDto,
            applicationDto = applicationDto,
            import = import
        )
        applicant = applicantService.save(applicant)
        application.applicant = applicant
        application.certificate = applicationDtoDataSource.getPrimaryCertificate(
            applicant = applicant,
            application = application,
            applicantDto = applicantDto,
            applicationDto = applicationDto,
            import = import
        )
        application.import = import
        processWarnings(applicant, application)
        potentialDuplicateFinder.findPotentialDuplicate(applicant, importId)
        application = applicationService.save(application)
        import.importedApplications++
    }

    private fun processWarnings(applicant: Applicant, application: Application) {
        val warnings = mutableListOf<String>()
        if (applicant.documents.size == 0) {
            warnings += "Brak dokumentów uprawniających do podjęcia studiów."
        }
        if (applicant.addresses.size == 0) {
            warnings += "Brak adresów."
        }
        if (applicant.highSchoolName == null && applicant.highSchoolUsosCode == null) {
            warnings += "Brak informacji o szkole średniej."
        }
        if (applicant.phoneNumbers.size == 0) {
            warnings += "Brak numerów telefonóœ."
        }
        if (applicant.photo.isNullOrBlank()) {
            warnings += "Brak zdjęcia."
        }
        if (applicant.dateOfBirth == null) {
            warnings += "Brak daty urodzenia."
        }
        application.warnings = warnings.joinToString("\n").takeIf {
            it.isNotBlank()
        }
    }

    @LogExecutionTime
    private fun createOrUpdateApplication(
        applicationDto: IApplicationDto,
        applicationDtoDataSource: IApplicationDataSource<IApplicationDto, IApplicantDto>,
        import: Import
    ): Application {
        val foundApplication = applicationService.findByForeignIdAndDataSourceId(
            applicationDto.foreignId,
            applicationDtoDataSource.id
        )
        if (foundApplication?.importStatus == ApplicationImportStatus.IMPORTED) {
            import.totalCount = import.totalCount!! - 1
            return foundApplication
        }
        return if (foundApplication != null) {
            applicationDtoDataSource.updateApplication(foundApplication, applicationDto)
        } else {
            applicationDtoDataSource.mapApplicationDtoToApplication(applicationDto).also {
                it.dataSourceId = applicationDtoDataSource.id
                it.editUrl = applicationDtoDataSource.getApplicationEditUrl(it.foreignId)
            }
        }
    }

    @LogExecutionTime
    private fun createOrUpdateApplicant(
        applicantDto: IApplicantDto,
        applicationDtoDataSource: IApplicationDataSource<IApplicationDto, IApplicantDto>,
        applicationDto: IApplicationDto
    ): Applicant {
        val foundApplicant = applicantService.findByForeignIdAndDataSourceId(
            applicantDto.foreignId,
            applicationDtoDataSource.id
        )
        return if (foundApplicant != null) {
            applicationDtoDataSource.updateApplicant(foundApplicant, applicantDto, applicationDto)
        } else {
            applicationDtoDataSource.mapApplicantDtoToApplicant(applicantDto).also {
                it.dataSourceId = applicationDtoDataSource.id
            }
        }
    }
}
