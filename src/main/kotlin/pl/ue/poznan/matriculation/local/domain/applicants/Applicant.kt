package pl.ue.poznan.matriculation.local.domain.applicants

import pl.ue.poznan.matriculation.irk.domain.applicants.Applicant
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
    constructor(applicant: Applicant) : this(
            email = applicant.email,
            indexNumber = applicant.indexNumber,
            password = applicant.password,
            name = Name(applicant.name),
            phone = applicant.phone,
            citizenship = applicant.citizenship,
            photo = applicant.photo,
            photoPermission = applicant.photoPermission,
            casPasswordOverride = applicant.casPasswordOverride,
            modificationDate = applicant.modificationDate,
            basicData = BasicData(applicant.basicData),
            contactData = ContactData(applicant.contactData!!),
            additionalData = AdditionalData(applicant.additionalData!!),
            foreignerData = ForeignerData(applicant.foreignerData!!),
            educationData = EducationData(applicant.educationData!!)
    )
}