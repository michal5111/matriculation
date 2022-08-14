package pl.poznan.ue.matriculation.applicantDataSources

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import pl.poznan.ue.matriculation.cem.domain.CemApplication
import pl.poznan.ue.matriculation.cem.domain.CemStudent
import pl.poznan.ue.matriculation.cem.enum.ApplicationStatus
import pl.poznan.ue.matriculation.cem.enum.CourseEditionStatus
import pl.poznan.ue.matriculation.cem.service.CemApplicationService
import pl.poznan.ue.matriculation.cem.service.CemStudentService
import pl.poznan.ue.matriculation.cem.service.CourseService
import pl.poznan.ue.matriculation.exception.ApplicantMappingException
import pl.poznan.ue.matriculation.exception.ApplicantNotFoundException
import pl.poznan.ue.matriculation.kotlinExtensions.parseAddressFromString
import pl.poznan.ue.matriculation.kotlinExtensions.trimPhoneNumber
import pl.poznan.ue.matriculation.local.domain.applicants.*
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.const.PhoneNumberType
import pl.poznan.ue.matriculation.local.domain.const.PhotoPermissionType
import pl.poznan.ue.matriculation.local.domain.enum.AddressType
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.ProgrammeDto
import pl.poznan.ue.matriculation.local.dto.RegistrationDto
import pl.poznan.ue.matriculation.oracle.service.CitizenshipService
import pl.poznan.ue.matriculation.oracle.service.ProgrammeService
import java.util.*

class CemApplicationDataSourceImpl(
    override val name: String,
    override val id: String,
    override val instanceUrl: String,
    private val cemApplicationService: CemApplicationService,
    private val cemStudentService: CemStudentService,
    private val courseService: CourseService,
    private val programmeService: ProgrammeService,
    private val applicationStatus: ApplicationStatus,
    private val citizenshipService: CitizenshipService
) : IApplicationDataSource<CemApplication, CemStudent> {

    private val nonAlphabeticalCharactersRegex = " *[-.,] *".toRegex()

    override fun getApplicationsPage(
        import: Import,
        registrationCode: String,
        programmeForeignId: String,
        pageNumber: Int
    ): IPage<CemApplication> {
        val pageable = PageRequest.of(pageNumber - 1, 50)
        val applicationsPage = cemApplicationService.findAllByCourseEditionIdAndStatus(
            pageable,
            registrationCode.toLong(),
            applicationStatus
        )
        return object : IPage<CemApplication> {
            override fun getTotalSize(): Int {
                return applicationsPage.totalElements.toInt()
            }

            override fun getContent(): List<CemApplication> {
                return applicationsPage.content
            }

            override fun hasNext(): Boolean {
                return applicationsPage.hasNext()
            }

        }
    }

    override fun getApplicantById(applicantId: Long, applicationDto: CemApplication): CemStudent {
        return cemStudentService.findById(applicantId) ?: throw ApplicantNotFoundException()
    }

    override fun postMatriculation(foreignApplicationId: Long): Int {
        return 0
    }

    override fun getAvailableRegistrationProgrammes(registration: String): List<ProgrammeDto> {
        return programmeService.findAllByCodeLike("P-%", Sort.by("code")).map {
            ProgrammeDto(it.code, it.code, it.description)
        }
    }

    override fun getAvailableRegistrations(): List<RegistrationDto> {
        return courseService.findAllByStatus(CourseEditionStatus.ACTIVE, Sort.by("name")).map {
            RegistrationDto(it.id.toString(), "${it.name} Numer: ${it.number}")
        }
    }

    override fun getApplicationById(applicationId: Long, applicationDto: CemApplication): CemApplication {
        return applicationDto
    }

    override fun mapApplicationDtoToApplication(applicationDto: CemApplication): Application {
        return Application(
            foreignId = applicationDto.foreignId,
            dataSourceId = id,
        )
    }

    override fun mapApplicantDtoToApplicant(applicantDto: CemStudent): Applicant {
        return Applicant(
            foreignId = applicantDto.foreignId,
            dataSourceId = id,
            email = applicantDto.email ?: throw ApplicantMappingException("Email is null"),
            given = applicantDto.firstName?.trim()
                ?: throw ApplicantMappingException("First name is null"),
            maiden = applicantDto.maidenName
                ?.takeIf { it.isNotBlank() && !it.matches(nonAlphabeticalCharactersRegex) }?.trim(),
            middle = applicantDto.secondName
                ?.takeIf { it.isNotBlank() && !it.matches(nonAlphabeticalCharactersRegex) }?.trim(),
            family = applicantDto.lastName?.trim()
                ?: throw ApplicantMappingException("Last name is null"),
            pesel = applicantDto.pesel?.takeIf { it.isNotBlank() }?.trim(),
            fathersName = applicantDto.fatherName
                ?.takeIf { it.isNotBlank() && !it.matches(nonAlphabeticalCharactersRegex) }?.trim(),
            mothersName = applicantDto.motherName
                ?.takeIf { it.isNotBlank() && !it.matches(nonAlphabeticalCharactersRegex) }?.trim(),
            modificationDate = Date(),
            dateOfBirth = applicantDto.birthdate,
            cityOfBirth = applicantDto.birthPlace
                ?.takeIf { it.isNotBlank() && !it.matches(nonAlphabeticalCharactersRegex) }?.trim(),
            photoPermission = PhotoPermissionType.NOBODY,
            sex = if (applicantDto.sex == 1) 'M' else 'K',
        ).apply {
            if (applicantDto.idNumber?.isNotBlank() == true) {
                val idDocument = IdentityDocument(
                    applicant = this,
                    country = null,
                    expDate = null,
                    number = applicantDto.idNumber,
                    type = 'C',
                    primaryIdApplicant = this
                )
                primaryIdentityDocument = idDocument
                addIdentityDocument(idDocument)
            }
            createAddresses(this, applicantDto)
            createPhoneNumbers(this, applicantDto)
        }
    }

    private fun createAddress(
        applicant: Applicant,
        address: String,
        city: String?,
        postalCode: String?,
        country: String?,
        addressType: AddressType
    ) {
        val parsedAddress = parseAddressFromString(address)
        applicant.addAddress(
            Address(
                applicant = applicant,
                addressType = addressType,
                city = city?.substringAfter(',')?.takeIf { it.isNotBlank() }?.trim(),
                postalCode = postalCode?.takeIf { it.isNotBlank() },
                cityIsCity = false,
                countryCode = country?.let {
                    citizenshipService.findByAnyName(it.trim())?.code
                },
                flatNumber = parsedAddress.flatNumber,
                street = parsedAddress.street,
                streetNumber = parsedAddress.streetNumber
            )
        )
    }

    private fun createAddresses(applicant: Applicant, applicantDto: CemStudent) {
        applicantDto.addressStreet?.trim()
            ?.takeIf { it.isNotBlank() }?.let { address ->
                createAddress(
                    applicant = applicant,
                    address = address,
                    city = applicantDto.addressCity,
                    postalCode = (applicantDto.addressPostalCode1 + applicantDto.addressPostalCode2)
                        .takeIf { it.isNotBlank() },
                    country = applicantDto.addressCountry,
                    addressType = AddressType.PERMANENT
                )
            }

        applicantDto.address2Street?.trim()
            ?.takeIf { it.isNotBlank() }?.let { address ->
                createAddress(
                    applicant = applicant,
                    address = address,
                    city = applicantDto.address2City,
                    postalCode = (applicantDto.address2PostalCode1 + applicantDto.address2PostalCode2)
                        .takeIf { it.isNotBlank() },
                    country = applicantDto.address2Country,
                    addressType = AddressType.CORRESPONDENCE
                )
            }
    }

    private fun createPhoneNumbers(applicant: Applicant, applicantDto: CemStudent) {
        applicantDto.phone?.trim()?.takeIf { it.isNotBlank() }?.trimPhoneNumber()?.let {
            applicant.addPhoneNumber(
                PhoneNumber(
                    applicant = applicant,
                    number = it,
                    comment = "Telefon",
                    phoneNumberType = PhoneNumberType.MOBILE
                )
            )
        }

        applicantDto.mobile?.trim()?.takeIf { it.isNotBlank() }?.trimPhoneNumber()?.let {
            applicant.addPhoneNumber(
                PhoneNumber(
                    applicant = applicant,
                    number = it,
                    comment = "Telefon komórkowy",
                    phoneNumberType = PhoneNumberType.MOBILE
                )
            )
        }
    }

    override fun updateApplication(
        application: Application,
        applicationDto: CemApplication
    ): Application {
        return application
    }

    override fun updateApplicant(
        applicant: Applicant,
        applicantDto: CemStudent,
        applicationDto: CemApplication
    ): Applicant {
        return applicant.apply {
            email = applicantDto.email ?: throw ApplicantMappingException("Email is null")
            given = applicantDto.firstName?.trim()
                ?: throw ApplicantMappingException("First name is null")
            maiden = applicantDto.maidenName
                ?.takeIf { it.isNotBlank() && !it.matches(nonAlphabeticalCharactersRegex) }?.trim()
            middle = applicantDto.secondName
                ?.takeIf { it.isNotBlank() && !it.matches(nonAlphabeticalCharactersRegex) }?.trim()
            family =
                applicantDto.lastName?.trim() ?: throw ApplicantMappingException("Last name is null")
            pesel = applicantDto.pesel
            fathersName = applicantDto.fatherName
                ?.takeIf { it.isNotBlank() && !it.matches(nonAlphabeticalCharactersRegex) }?.trim()
            mothersName = applicantDto.motherName
                ?.takeIf { it.isNotBlank() && !it.matches(nonAlphabeticalCharactersRegex) }?.trim()
            modificationDate = Date()
            dateOfBirth = applicantDto.birthdate
            cityOfBirth = applicantDto.birthPlace
                ?.takeIf { it.isNotBlank() && !it.matches(nonAlphabeticalCharactersRegex) }?.trim()
            photoPermission = PhotoPermissionType.NOBODY
            sex = if (applicantDto.sex == 1) 'M' else 'K'
            createAddresses(this, applicantDto)
            createPhoneNumbers(this, applicantDto)
        }
    }

    override fun getPrimaryCertificate(
        application: Application,
        applicationDto: CemApplication,
        applicant: Applicant,
        applicantDto: CemStudent,
        import: Import
    ): Document? {
        return null
    }

    override fun getPrimaryIdentityDocument(
        application: Application,
        applicationDto: CemApplication,
        applicant: Applicant,
        applicantDto: CemStudent,
        import: Import
    ): IdentityDocument? {
        return applicant.primaryIdentityDocument
    }

    override fun getApplicationEditUrl(applicationId: Long): String? {
        return null
    }
}
