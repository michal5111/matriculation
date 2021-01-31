package pl.poznan.ue.matriculation.dreamApply.mapper

import pl.poznan.ue.matriculation.dreamApply.dto.applicant.DreamApplyApplicantDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.DreamApplyApplicationDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.EducationLevelType
import pl.poznan.ue.matriculation.kotlinExtensions.nameCapitalize
import pl.poznan.ue.matriculation.local.domain.applicants.*
import pl.poznan.ue.matriculation.local.domain.const.BasicDataDatasourceType
import pl.poznan.ue.matriculation.local.domain.const.IdentityDocumentType
import pl.poznan.ue.matriculation.local.domain.const.PhoneNumberType
import pl.poznan.ue.matriculation.local.domain.const.PhotoPermissionType
import pl.poznan.ue.matriculation.local.domain.enum.AddressType
import pl.poznan.ue.matriculation.oracle.repo.SchoolRepository
import java.util.*

open class DreamApplyApplicantMapper(val schoolRepository: SchoolRepository) {

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
        val profile = application.profile ?: throw java.lang.IllegalStateException("Profile is null")
        return Applicant(
            foreignId = dreamApplyApplicantDto.id,
            name = Name(
                given = dreamApplyApplicantDto.name.given.nameCapitalize(),
                middle = dreamApplyApplicantDto.name.middle.takeUnless {
                    it.isNullOrBlank()
                }?.nameCapitalize(),
                family = dreamApplyApplicantDto.name.family.nameCapitalize(),
                maiden = null
            ),
            email = dreamApplyApplicantDto.email,
            photo = dreamApplyApplicantDto.photo,
            phone = dreamApplyApplicantDto.phone?.replace(" ", ""),
            citizenship = dreamApplyApplicantDto.citizenship,
            nationality = profile.nationality,
            photoPermission = PhotoPermissionType.NOBODY,
            password = null,
            modificationDate = dreamApplyApplicantDto.registered,
            basicData = BasicData(
                cityOfBirth = profile.birth?.place,
                countryOfBirth = profile.birth?.country,
                dateOfBirth = profile.birth?.date,
                pesel = profile.nationalidcode?.polish,
                sex = if (profile.gender == "M") 'M' else 'K',
                dataSource = BasicDataDatasourceType.USER
            ),
            additionalData = AdditionalData(
                mothersName = profile.family?.mother ?: application.extras?.find {
                    it.name == "Mother's name"
                }?.value,
                fathersName = profile.family?.father ?: application.extras?.find {
                    it.name == "Father's name"
                }?.value,
                wku = null,
                militaryCategory = null,
                militaryStatus = null
            ),
            indexNumber = null,
            casPasswordOverride = null,
            educationData = EducationData(),
            applicantForeignerData = null
        ).apply {
            educationData.applicant = this
            additionalData.applicant = this
            basicData.applicant = this
            name.applicant = this
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
            name.apply {
                given = applicant.name.given.nameCapitalize()
                middle = applicant.name.middle?.nameCapitalize()
                family = applicant.name.family.nameCapitalize()
            }
            email = dreamApplyApplicantDto.email
            photo = dreamApplyApplicantDto.photo
            phone = dreamApplyApplicantDto.phone?.replace(" ", "")
            citizenship = dreamApplyApplicantDto.citizenship
            nationality = profile.nationality
            basicData.apply {
                pesel = profile.nationalidcode?.polish
                cityOfBirth = profile.birth?.place
                countryOfBirth = profile.birth?.country
                dateOfBirth = profile.birth?.date
                sex = if (profile.gender == "M") 'M' else 'K'
            }
            additionalData.apply {
                mothersName = profile.family?.mother ?: application.extras?.find {
                    it.name == "Mother's name"
                }?.value
                fathersName = profile.family?.father ?: application.extras?.find {
                    it.name == "Father's name"
                }?.value
            }
            phoneNumbers.clear()
            createPhoneNumbers(
                applicant = this,
                day = application.contact?.telephone?.day,
                evening = application.contact?.telephone?.evening,
                mobile = application.contact?.telephone?.mobile,
                fax = application.contact?.telephone?.fax,
                emergency = application.contact?.emergency?.telephone
            )
            addresses.clear()
            addAddress(this, application)
            applicant.educationData.documents.clear()
            createEducationData(applicant, application)
            applicant.identityDocuments.clear()
            createIdentityDocuments(this, application)
            addBaseOfStay(this, application)
        }
    }

    private fun createIdentityDocuments(applicant: Applicant, applicationDto: DreamApplyApplicationDto) {
        applicationDto.profile?.passport?.let {
            it.idcard?.run {
                applicant.identityDocuments.add(
                    IdentityDocument(
                        type = IdentityDocumentType.ID_CARD,
                        number = this.replace(" ", ""),
                        country = null,
                        expDate = null,
                        applicant = applicant
                    )
                )
            }
            it.number?.run {
                applicant.identityDocuments.add(
                    IdentityDocument(
                        type = IdentityDocumentType.PASSPORT,
                        country = it.country,
                        expDate = it.expiry,
                        applicant = applicant,
                        number = this.replace(" ", "")
                    )
                )
            }
        }
    }

    private fun createEducationData(applicant: Applicant, applicationDto: DreamApplyApplicationDto) {
        applicant.educationData.apply {
            applicationDto.education?.find {
                it.level == EducationLevelType.SE
            }?.let {
                highSchoolCity = it.city
                highSchoolName = it.institution
            }
        }

        applicationDto.education?.filter {
            !it.diploma?.number.isNullOrBlank() && it.level != null && it.diploma?.issue?.date != null
        }?.map {
            Document(
                educationData = applicant.educationData,
                certificateType = it.level!!.levelName,
                certificateTypeCode = it.level.toString(),
                certificateUsosCode = it.level.usosCode ?: when {
                    it.diploma!!.number!!.matches(maturaRegex) -> 'N'
                    else -> 'Z'
                },
                comment = null,
                documentNumber = it.diploma!!.number!!,
                documentYear = it.diploma.issue!!.date!!.let { date ->
                    val cal = Calendar.getInstance()
                    cal.time = date
                    cal.get(Calendar.YEAR)
                },
                issueCity = it.city,
                issueCountry = it.country,
                issueDate = it.diploma.issue.date!!,
                issueInstitution = it.institution,
                issueInstitutionUsosCode = applicationDto.extras?.find { extraDto ->
                    extraDto.id == 212L && extraDto.name == "Please select your OKE"
                }?.value?.let { okeName ->
                    okeMap[okeName]
                } ?: it.institution?.let { schoolName ->
                    schoolRepository.findByNameIgnoreCase("%${schoolName.trim()}%")
                }.takeIf { idList ->
                    idList?.size == 1
                }?.first()
                ?: if (it.institution?.trim()?.toUpperCase() == "POZNAN UNIVERSITY OF ECONOMICS AND BUSINESS") 110825L
                else null,
                modificationDate = Date()
            )
        }?.let {
            applicant.educationData.documents.addAll(it)
        }
    }

    private fun createPhoneNumber(applicant: Applicant, number: String?, type: String, comment: String) {
        if (!number.isNullOrBlank()) {
            applicant.phoneNumbers.add(
                PhoneNumber(
                    number = number.replace(" ", ""),
                    phoneNumberType = type,
                    comment = comment,
                    applicant = applicant
                )
            )
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
                    city = addressDto.city,
                    cityIsCity = false,
                    countryCode = addressDto.country,
                    postalCode = addressDto.postalcode?.trim()?.replace("-", ""),
                    street = addressDto.street?.replace("\n", " "),
                    streetNumber = addressDto.house,
                    flatNumber = addressDto.apartment
                ),
                Address(
                    applicant = applicant,
                    addressType = AddressType.CORRESPONDENCE,
                    city = addressDto.correspondence?.city,
                    cityIsCity = false,
                    countryCode = addressDto.correspondence?.country,
                    postalCode = addressDto.correspondence?.postalcode,
                    street = addressDto.correspondence?.street?.replace("\n", " "),
                    streetNumber = addressDto.correspondence?.house ?: dreamApplyApplication.extras?.find {
                        it.name == "House number"
                    }?.value,
                    flatNumber = addressDto.correspondence?.apartment
                )
            ).let {
                applicant.addresses.addAll(it)
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
                applicant.applicantForeignerData!!.apply {
                    baseOfStay = "OKP"
                }
            }
        }
    }
}