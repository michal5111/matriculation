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
import java.text.SimpleDateFormat
import java.util.*

class DreamApplyApplicantMapper {

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun map(dreamApplyApplicantDto: DreamApplyApplicantDto): Applicant {
        val application = dreamApplyApplicantDto.dreamApplyApplication ?: throw java.lang.IllegalStateException("Application is null")
        val profile = application.profile ?: throw java.lang.IllegalStateException("Profile is null")
        return Applicant(
                foreignId = dreamApplyApplicantDto.id,
                name = Name(
                        given = dreamApplyApplicantDto.name.given.nameCapitalize(),
                        middle = dreamApplyApplicantDto.name.middle.takeIf {
                            !it.isNullOrBlank()
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
                        dateOfBirth = profile.birth?.date?.let { birthDate ->
                            simpleDateFormat.parse(birthDate)
                        },
                        pesel = profile.nationalidcode?.polish,
                        sex = if (profile.gender == 'M') 'M' else 'K',
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
                educationData = EducationData(
                        highSchoolCity = null,
                        highSchoolName = null,
                        highSchoolType = null,
                        highSchoolUsosCode = null,
                        documents = mutableListOf()
                ),
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

    fun update(applicant: Applicant, dreamApplyApplicantDto: DreamApplyApplicantDto): Applicant {
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
                dateOfBirth = profile.birth?.date?.let { birthDate ->
                    simpleDateFormat.parse(birthDate)
                }
                sex = if (profile.gender == 'M') 'M' else 'K'
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
            if (it.idcard != null) {
                applicant.identityDocuments.add(
                        IdentityDocument(
                                type = IdentityDocumentType.ID_CARD,
                                number = it.idcard,
                                country = null,
                                expDate = null,
                                applicant = applicant
                        )
                )
            }
            if (it.number != null) {
                applicant.identityDocuments.add(
                        IdentityDocument(
                                type = IdentityDocumentType.PASSPORT,
                                country = it.country,
                                expDate = it.expiry?.let { expiryDate ->
                                    simpleDateFormat.parse(expiryDate)
                                },
                                applicant = applicant,
                                number = it.number
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
        applicant.educationData.documents.addAll(
                applicationDto.education?.filter {
                    !it.diploma?.number.isNullOrBlank() && it.level != null && !it.diploma?.issue?.date.isNullOrBlank()
                            && it.level.usosCode != null
                }?.map {
                    Document(
                            educationData = applicant.educationData,
                            certificateType = it.level!!.levelName,
                            certificateTypeCode = it.level.toString(),
                            certificateUsosCode = it.level.usosCode,
                            comment = null,
                            documentNumber = it.diploma!!.number!!,
                            documentYear = it.diploma.issue!!.date!!.let { dateString ->
                                val date = simpleDateFormat.parse(dateString)
                                val cal = Calendar.getInstance()
                                cal.time = date
                                cal.get(Calendar.YEAR)
                            },
                            issueCity = it.city,
                            issueCountry = it.country,
                            issueDate = simpleDateFormat.parse(it.diploma.issue.date),
                            issueInstitution = it.institution,
                            issueInstitutionUsosCode = null,
                            modificationDate = Date()
                    )
                }?.toMutableList() ?: mutableListOf()
        )
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
            applicant.addresses.add(
                    Address(
                            applicant = applicant,
                            addressType = AddressType.RESIDENCE,
                            city = addressDto.city,
                            cityIsCity = false,
                            countryCode = addressDto.country,
                            postalCode = addressDto.postalcode,
                            street = addressDto.street,
                            streetNumber = addressDto.house,
                            flatNumber = addressDto.apartment
                    )
            )
            applicant.addresses.add(
                    Address(
                            applicant = applicant,
                            addressType = AddressType.CORRESPONDENCE,
                            city = addressDto.correspondence?.city,
                            cityIsCity = false,
                            countryCode = addressDto.correspondence?.country,
                            postalCode = addressDto.correspondence?.postalcode,
                            street = addressDto.correspondence?.street,
                            streetNumber = addressDto.correspondence?.house ?: dreamApplyApplication.extras?.find {
                                it.name == "House number"
                            }?.value,
                            flatNumber = addressDto.correspondence?.apartment
                    )
            )
        }
        applicant.addresses.removeAll {
            it.countryCode.isNullOrBlank() || it.street.isNullOrBlank()
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