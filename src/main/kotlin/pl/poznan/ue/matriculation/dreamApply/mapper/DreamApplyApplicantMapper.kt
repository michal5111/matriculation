package pl.poznan.ue.matriculation.dreamApply.mapper

import pl.poznan.ue.matriculation.dreamApply.dto.applicant.DreamApplyApplicantDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.DreamApplyApplicationDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.EducationLevelType
import pl.poznan.ue.matriculation.kotlinExtensions.nameCapitalize
import pl.poznan.ue.matriculation.kotlinExtensions.trimPhoneNumber
import pl.poznan.ue.matriculation.kotlinExtensions.trimPostalCode
import pl.poznan.ue.matriculation.local.domain.applicants.*
import pl.poznan.ue.matriculation.local.domain.const.IdentityDocumentType
import pl.poznan.ue.matriculation.local.domain.const.PhoneNumberType
import pl.poznan.ue.matriculation.local.domain.const.PhotoPermissionType
import pl.poznan.ue.matriculation.local.domain.enum.AddressType
import pl.poznan.ue.matriculation.oracle.repo.SchoolRepository
import java.util.*

open class DreamApplyApplicantMapper(private val schoolRepository: SchoolRepository) {

    private val maturaRegex = "M/[0-9]{8}/[0-9]{2}".toRegex()
    private val okeMap = mapOf(
        "OKE Gdańsk" to 18243L,
        "OKE Jaworzno" to 18244L,
        "OKE Kraków" to 18245L,
        "OKE Łomża" to 18246L,
        "OKE Łódź" to 18247L,
        "OKE Poznań" to 18248L,
        "OKE Warszawa" to 18250L,
        "OKE Wrocław" to 18249L
    )

    open fun map(dreamApplyApplicantDto: DreamApplyApplicantDto): Applicant {
        val application = dreamApplyApplicantDto.dreamApplyApplication
            ?: throw java.lang.IllegalStateException("Application is null")
        val profile = application.profile ?: throw IllegalStateException("Profile is null")
        return Applicant(
            foreignId = dreamApplyApplicantDto.id,
            given = dreamApplyApplicantDto.name.given.nameCapitalize(),
            middle = dreamApplyApplicantDto.name.middle.takeUnless {
                it.isNullOrBlank()
            }?.nameCapitalize(),
            family = dreamApplyApplicantDto.name.family.nameCapitalize(),
            maiden = null,
            email = dreamApplyApplicantDto.email.trim(),
            photo = dreamApplyApplicantDto.photo,
            citizenship = dreamApplyApplicantDto.citizenship,
            nationality = profile.nationality,
            photoPermission = PhotoPermissionType.NOBODY,
            password = null,
            modificationDate = dreamApplyApplicantDto.registered,

            cityOfBirth = profile.birth?.place?.trim(),
            countryOfBirth = profile.birth?.country,
            dateOfBirth = profile.birth?.date,
            pesel = profile.nationalidcode?.polish?.trim(),
            sex = if (profile.gender == "M") 'M' else 'K',
            mothersName = profile.family?.mother ?: application.extras?.find {
                it.name == "Mother's name"
            }?.value?.trim(),
            fathersName = profile.family?.father ?: application.extras?.find {
                it.name == "Father's name"
            }?.value?.trim(),
            wku = null,
            militaryCategory = null,
            militaryStatus = null,
            indexNumber = null,
            applicantForeignerData = null
        ).apply {
            addAddress(this, application)
            createPhoneNumbers(
                applicant = this,
                day = application.contact?.telephone?.day,
                evening = application.contact?.telephone?.evening,
                mobile = application.contact?.telephone?.mobile,
                fax = application.contact?.telephone?.fax,
                emergency = application.contact?.emergency?.telephone
            )
            createEducationData(this, application)
            createIdentityDocuments(this, application)
            addBaseOfStay(this, application)
        }
    }

    open fun update(applicant: Applicant, dreamApplyApplicantDto: DreamApplyApplicantDto): Applicant {
        val application = dreamApplyApplicantDto.dreamApplyApplication
            ?: throw IllegalStateException("Application is null")
        val profile = application.profile ?: throw java.lang.IllegalStateException("Profile is null")
        return applicant.apply {

            given = applicant.given.nameCapitalize()
            middle = applicant.middle?.nameCapitalize()
            family = applicant.family.nameCapitalize()

            email = dreamApplyApplicantDto.email.trim()
            photo = dreamApplyApplicantDto.photo
            citizenship = dreamApplyApplicantDto.citizenship
            nationality = profile.nationality

            pesel = profile.nationalidcode?.polish?.trim()
            cityOfBirth = profile.birth?.place?.trim()
            countryOfBirth = profile.birth?.country
            dateOfBirth = profile.birth?.date
            sex = if (profile.gender == "M") 'M' else 'K'


            mothersName = profile.family?.mother ?: application.extras?.find {
                it.name == "Mother's name"
            }?.value?.trim()
            fathersName = profile.family?.father ?: application.extras?.find {
                it.name == "Father's name"
            }?.value?.trim()

            createPhoneNumbers(
                applicant = this,
                day = application.contact?.telephone?.day?.replace(" ", ""),
                evening = application.contact?.telephone?.evening?.replace(" ", ""),
                mobile = application.contact?.telephone?.mobile?.replace(" ", ""),
                fax = application.contact?.telephone?.fax?.replace(" ", ""),
                emergency = application.contact?.emergency?.telephone?.replace(" ", "")
            )
            addAddress(this, application)
            createEducationData(applicant, application)
            createIdentityDocuments(this, application)
            addBaseOfStay(this, application)
        }
    }

    private fun createIdentityDocuments(applicant: Applicant, applicationDto: DreamApplyApplicationDto) {
        applicationDto.profile?.passport?.let {
            it.idcard?.run {
                applicant.addIdentityDocument(
                    IdentityDocument(
                        type = IdentityDocumentType.ID_CARD,
                        number = this.replace("[^a-zA-Z0-9]+", ""),
                        country = null,
                        expDate = null,
                        applicant = applicant
                    )
                )
            }
            it.number?.run {
                applicant.addIdentityDocument(
                    IdentityDocument(
                        type = IdentityDocumentType.PASSPORT,
                        country = it.country,
                        expDate = it.expiry,
                        applicant = applicant,
                        number = this.replace("[^a-zA-Z0-9]+", "")
                    )
                )
            }
        }
    }

    private fun createEducationData(applicant: Applicant, applicationDto: DreamApplyApplicationDto) {
        applicant.apply {
            applicationDto.education?.find {
                it.level == EducationLevelType.SE
            }?.let {
                highSchoolCity = it.city?.trim()
                highSchoolName = it.institution?.trim()
            }
        }

        applicationDto.education?.filter {
            !it.diploma?.number.isNullOrBlank() && it.level != null && it.diploma?.issue?.date != null
        }?.map {
            Document(
                certificateType = it.level!!.levelName,
                certificateTypeCode = it.level.toString(),
                certificateUsosCode = it.level.usosCode ?: when {
                    it.diploma!!.number!!.trim().matches(maturaRegex) -> 'N'
                    else -> 'Z'
                },
                comment = null,
                documentNumber = it.diploma!!.number!!.trim(),
                documentYear = it.diploma.issue!!.date!!.let { date ->
                    val cal = Calendar.getInstance()
                    cal.time = date
                    cal.get(Calendar.YEAR)
                },
                issueCity = it.city?.trim(),
                issueCountry = it.country?.trim(),
                issueDate = it.diploma.issue.date!!,
                issueInstitution = it.institution?.trim(),
                issueInstitutionUsosCode = applicationDto.extras?.find { extraDto ->
                    extraDto.id == 212L && extraDto.name == "Please select your OKE"
                }?.value?.let { okeName ->
                    okeMap[okeName]
                } ?: it.institution?.let { schoolName ->
                    schoolRepository.findByNameIgnoreCase("%${schoolName.trim()}%")
                }.takeIf { idList ->
                    idList?.size == 1
                }?.first()
                ?: if (it.institution?.trim()
                        ?.uppercase(Locale.getDefault()) == "POZNAN UNIVERSITY OF ECONOMICS AND BUSINESS"
                ) 110825L
                else null,
                modificationDate = Date()
            )
        }?.forEach {
            applicant.addDocument(it)
        }
    }

    private fun createPhoneNumber(applicant: Applicant, number: String?, type: String, comment: String) {
        number?.trimPhoneNumber()?.let {
            PhoneNumber(
                number = it,
                phoneNumberType = type,
                comment = comment,
                applicant = applicant
            )
        }?.let {
            applicant.addPhoneNumber(it)
        }
    }

    private fun createPhoneNumbers(
        applicant: Applicant,
        day: String?,
        evening: String?,
        mobile: String?,
        fax: String?,
        emergency: String?
    ) {
        createPhoneNumber(
            applicant,
            day,
            PhoneNumberType.LANDLINE,
            "Dzienny numer telefonu stacjonarnego"
        )
        createPhoneNumber(
            applicant,
            evening,
            PhoneNumberType.LANDLINE,
            "Wieczorowy numer telefonu stacjonarnego"
        )
        createPhoneNumber(
            applicant,
            mobile,
            PhoneNumberType.MOBILE,
            "Numer telefonu komórkowego"
        )
        createPhoneNumber(
            applicant,
            fax,
            PhoneNumberType.FAX,
            "Numer faxu"
        )
        createPhoneNumber(
            applicant,
            emergency,
            PhoneNumberType.MOBILE,
            "Numer na nagłe wypadki"
        )
    }

    private fun addAddress(applicant: Applicant, dreamApplyApplication: DreamApplyApplicationDto) {
        dreamApplyApplication.contact?.address?.let { addressDto ->
            mutableListOf(
                Address(
                    applicant = applicant,
                    addressType = AddressType.RESIDENCE,
                    city = addressDto.city?.trim(),
                    cityIsCity = false,
                    countryCode = addressDto.country,
                    postalCode = addressDto.postalcode?.trimPostalCode(),
                    street = addressDto.street?.replace("\n", " "),
                    streetNumber = addressDto.house?.trim(),
                    flatNumber = addressDto.apartment?.trim()
                ),
                Address(
                    applicant = applicant,
                    addressType = AddressType.CORRESPONDENCE,
                    city = addressDto.correspondence?.city?.trim(),
                    cityIsCity = false,
                    countryCode = addressDto.correspondence?.country,
                    postalCode = addressDto.correspondence?.postalcode?.trimPostalCode(),
                    street = addressDto.correspondence?.street?.replace("\n", " "),
                    streetNumber = addressDto.correspondence?.house ?: dreamApplyApplication.extras?.find {
                        it.name == "House number"
                    }?.value,
                    flatNumber = addressDto.correspondence?.apartment?.trim()
                )
            ).filterNot {
                it.city.isNullOrBlank() && it.countryCode.isNullOrBlank() && it.flatNumber.isNullOrBlank()
                    && it.postalCode.isNullOrBlank() && it.street.isNullOrBlank() && it.streetNumber.isNullOrBlank()
            }.forEach {
                applicant.addAddress(it)
            }
        }
    }

    private fun addBaseOfStay(applicant: Applicant, dreamApplyApplicationDto: DreamApplyApplicationDto) {
        val polishCardAnswer = dreamApplyApplicationDto.extras?.find {
            it.name == "Do you have Karta Polaka?"
        }?.value
        if (polishCardAnswer == "Yes") {
            if (applicant.applicantForeignerData == null) {
                applicant.applicantForeignerData = ApplicantForeignerData(
                    baseOfStay = "OKP",
                    polishCardIssueCountry = null,
                    polishCardIssueDate = null,
                    polishCardNumber = null,
                    polishCardValidTo = null,
                    applicant = applicant
                )
            } else {
                applicant.applicantForeignerData?.apply {
                    baseOfStay = "OKP"
                }
            }
        }
    }
}
