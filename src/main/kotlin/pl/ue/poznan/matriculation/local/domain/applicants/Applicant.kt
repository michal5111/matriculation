package pl.ue.poznan.matriculation.local.domain.applicants

import pl.ue.poznan.matriculation.irk.dto.applicants.ApplicantDTO
import java.util.*
import javax.persistence.*

@Entity
data class Applicant(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,

        val email: String,

        val indexNumber: String?,

        val password: String,

        @OneToOne
        val name: Name,

        val phone: String?,

        val citizenship: String?,

        val photo: String?,

        val photoPermission: String?,

        val casPasswordOverride: String?,

        val modificationDate: Date,

        @OneToOne
        val basicData: BasicData,

        @OneToOne
        val contactData: ContactData?,

        @OneToOne
        val additionalData: AdditionalData?,

        @OneToOne
        val foreignerData: ForeignerData?,

        @OneToOne
        val educationData: EducationData?
) {
    constructor(applicantDTO: ApplicantDTO) : this(
            email = applicantDTO.email,
            indexNumber = applicantDTO.indexNumber,
            password = applicantDTO.password,
            name = Name(applicantDTO.name),
            phone = applicantDTO.phone,
            citizenship = applicantDTO.citizenship,
            photo = applicantDTO.photo,
            photoPermission = applicantDTO.photoPermission,
            casPasswordOverride = applicantDTO.casPasswordOverride,
            modificationDate = applicantDTO.modificationDate,
            basicData = BasicData(applicantDTO.basicData),
            contactData = ContactData(applicantDTO.contactData!!),
            additionalData = AdditionalData(applicantDTO.additionalData!!),
            foreignerData = ForeignerData(applicantDTO.foreignerData!!),
            educationData = EducationData(applicantDTO.educationData!!)
    )
}