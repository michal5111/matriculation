package pl.poznan.ue.matriculation.dreamApply.mapper

import pl.poznan.ue.matriculation.dreamApply.dto.applicant.DreamApplyApplicantDto
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
                phone = dreamApplyApplicantDto.phone,
                citizenship = dreamApplyApplicantDto.citizenship,
                nationality = application.profile.nationality,
                photoPermission = PhotoPermissionType.NOBODY,
                password = null,
                modificationDate = dreamApplyApplicantDto.registered,
                basicData = application.let {
                    BasicData(
                            cityOfBirth = it.profile!!.birth?.place,
                            countryOfBirth = it.profile.nationality ?: dreamApplyApplicantDto.citizenship,
                            dateOfBirth = it.profile.birth?.date?.let { birthDate ->
                                simpleDateFormat.parse(birthDate)
                            },
                            pesel = null,
                            sex = if (it.profile.gender == 'M') 'M' else 'K',
                            dataSource = BasicDataDatasourceType.USER
                    )
                },
                additionalData = application.let {
                    AdditionalData(
                            documentType = IdentityDocumentType.PASSPORT,
                            documentCountry = null,
                            documentExpDate = it.profile!!.passport?.expiry?.let { expiryDate ->
                                simpleDateFormat.parse(expiryDate)
                            },
                            documentNumber = it.profile.passport?.number,
                            mothersName = it.profile.family?.mother,
                            fathersName = it.profile.family?.father,
                            wku = null,
                            militaryCategory = null,
                            militaryStatus = null,
                            cityOfBirth = it.profile.birth?.place,
                            countryOfBirth = it.profile.nationality
                    )
                },
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
            addAddress(this, dreamApplyApplicantDto)
            createPhoneNumbers(
                    applicant = this,
                    day = application.contact?.telephone?.day,
                    evening = application.contact?.telephone?.evening,
                    mobile = application.contact?.telephone?.mobile,
                    fax = application.contact?.telephone?.fax,
                    emergency = application.contact?.emergency?.telephone
            )
        }
    }

    fun update(applicant: Applicant, dreamApplyApplicantDto: DreamApplyApplicantDto): Applicant {
        val application = dreamApplyApplicantDto.dreamApplyApplication ?: throw IllegalStateException("Application is null")
        return applicant.apply {
            name.apply {
                given = applicant.name.given.nameCapitalize()
                middle = applicant.name.middle?.nameCapitalize()
                family = applicant.name.family.nameCapitalize()
            }
            email = dreamApplyApplicantDto.email
            photo = dreamApplyApplicantDto.photo
            photo = dreamApplyApplicantDto.phone
            citizenship = dreamApplyApplicantDto.citizenship
            nationality = application.profile!!.nationality
            basicData.apply {
                cityOfBirth = application.profile.birth?.place
                countryOfBirth = application.profile.nationality ?: dreamApplyApplicantDto.citizenship
                dateOfBirth = application.profile.birth?.date?.let { birthDate ->
                    simpleDateFormat.parse(birthDate)
                }
                sex = if (application.profile.gender == 'M') 'M' else 'K'
            }
            additionalData.apply {
                documentExpDate = application.profile.passport?.expiry?.let {
                    simpleDateFormat.parse(it)
                }
                documentNumber = application.profile.passport?.number
                mothersName = application.profile.family?.mother
                fathersName = application.profile.family?.father
                cityOfBirth = application.profile.birth?.place
                countryOfBirth = application.profile.nationality
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
            addAddress(this, dreamApplyApplicantDto)
        }
    }

    private fun createPhoneNumber(applicant: Applicant, number: String?, type: String, comment: String) {
        if (!number.isNullOrBlank()) {
            applicant.phoneNumbers.add(
                    PhoneNumber(
                            number = number,
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

    private fun addAddress(applicant: Applicant, dreamApplyApplicantDto: DreamApplyApplicantDto) {
        dreamApplyApplicantDto.dreamApplyApplication!!.let {
            applicant.addresses.add(
                    Address(
                            applicant = applicant,
                            addressType = AddressType.RESIDENCE,
                            city = it.contact?.address?.municipality,
                            cityIsCity = false,
                            countryCode = it.contact?.address?.country,
                            postalCode = it.contact?.address?.postalcode,
                            street = it.contact?.address?.street,
                            streetNumber = it.contact?.address?.street,
                            flatNumber = null
                    )
            )
        }
    }
}