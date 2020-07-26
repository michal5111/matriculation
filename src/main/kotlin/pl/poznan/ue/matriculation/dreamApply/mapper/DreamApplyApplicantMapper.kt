package pl.poznan.ue.matriculation.dreamApply.mapper

import pl.poznan.ue.matriculation.dreamApply.dto.applicant.ApplicantDto
import pl.poznan.ue.matriculation.local.domain.applicants.*
import pl.poznan.ue.matriculation.local.domain.const.BasicDataDatasourceType
import pl.poznan.ue.matriculation.local.domain.const.IdentityDocumentType
import pl.poznan.ue.matriculation.local.domain.const.PhoneNumberType
import pl.poznan.ue.matriculation.local.domain.const.PhotoPermissionType
import pl.poznan.ue.matriculation.local.domain.enum.AddressType
import java.text.SimpleDateFormat
import java.util.*

class DreamApplyApplicantMapper {

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T", Locale.US)

    fun map(applicantDto: ApplicantDto): Applicant {
        val application = applicantDto.application ?: throw java.lang.IllegalStateException("Application is null")
        return Applicant(
                foreignId = applicantDto.id,
                name = Name(
                        given = applicantDto.name.given,
                        middle = applicantDto.name.middle,
                        family = applicantDto.name.family,
                        maiden = null
                ),
                email = applicantDto.email,
                photo = applicantDto.photo,
                phone = applicantDto.phone,
                citizenship = applicantDto.citizenship,
                nationality = application.profile.nationality,
                photoPermission = PhotoPermissionType.NOBODY,
                password = null,
                modificationDate = applicantDto.registered,
                basicData = application.let {
                    BasicData(
                            cityOfBirth = it.profile.birth.place,
                            countryOfBirth = it.profile.nationality,
                            dateOfBirth = simpleDateFormat.parse(it.profile.birth.date),
                            pesel = null,
                            sex = if (it.profile.gender == 'M') 'M' else 'K',
                            dataSource = BasicDataDatasourceType.USER
                    )
                },
                additionalData = application.let {
                    AdditionalData(
                            documentType = IdentityDocumentType.PASSPORT,
                            documentCountry = null,
                            documentExpDate = simpleDateFormat.parse(it.profile.passport.expiry),
                            documentNumber = it.profile.passport.number,
                            mothersName = it.profile.family.mother,
                            fathersName = it.profile.family.father,
                            wku = null,
                            militaryCategory = null,
                            militaryStatus = null,
                            cityOfBirth = it.profile.birth.place,
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
            addAddress(this, applicantDto)
            createPhoneNumbers(
                    applicant = this,
                    day = application.contact.telephone.day,
                    evening = application.contact.telephone.evening,
                    mobile = application.contact.telephone.mobile,
                    fax = application.contact.telephone.fax,
                    emergency = application.contact.emergency.telephone
            )
        }
    }

    fun update(applicant: Applicant, applicantDto: ApplicantDto): Applicant {
        val application = applicantDto.application ?: throw IllegalStateException("Application is null")
        return applicant.apply {
            name.apply {
                given = applicant.name.given
                middle = applicant.name.middle
                family = applicant.name.family
            }
            email = applicantDto.email
            photo = applicantDto.photo
            photo = applicantDto.phone
            citizenship = applicantDto.citizenship
            nationality = application.profile.nationality
            basicData.apply {
                cityOfBirth = application.profile.birth.place
                countryOfBirth = application.profile.nationality
                dateOfBirth = simpleDateFormat.parse(application.profile.birth.date)
                sex = if (application.profile.gender == 'M') 'M' else 'K'
            }
            additionalData.apply {
                documentExpDate = simpleDateFormat.parse(application.profile.passport.expiry)
                documentNumber = application.profile.passport.number
                mothersName = application.profile.family.mother
                fathersName = application.profile.family.father
                cityOfBirth = application.profile.birth.place
                countryOfBirth = application.profile.nationality
            }
            phoneNumbers.clear()
            createPhoneNumbers(
                    applicant = this,
                    day = application.contact.telephone.day,
                    evening = application.contact.telephone.evening,
                    mobile = application.contact.telephone.mobile,
                    fax = application.contact.telephone.fax,
                    emergency = application.contact.emergency.telephone
            )
            addresses.clear()
            addAddress(this, applicantDto)
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

    private fun addAddress(applicant: Applicant, applicantDto: ApplicantDto) {
        applicantDto.application!!.let {
            applicant.addresses.add(
                    Address(
                            applicant = applicant,
                            addressType = AddressType.RESIDENCE,
                            city = it.contact.address.municipality,
                            cityIsCity = false,
                            countryCode = it.contact.address.country,
                            postalCode = it.contact.address.postalcode,
                            street = it.contact.address.street,
                            streetNumber = it.contact.address.street,
                            flatNumber = null
                    )
            )
        }
    }
}