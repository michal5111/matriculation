package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.poznan.ue.matriculation.local.domain.applications.Application
import java.util.*
import javax.persistence.*

@Entity
class Applicant(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(unique = true)
        val irkId: Long,

        var email: String,

        var indexNumber: String?,

        var password: String,

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        val name: Name,

        var phone: String?,

        var citizenship: String?,

        var photo: String?,

//        @JsonIgnore
//        @Basic(fetch = FetchType.LAZY)
//        @Lob
//        @Column(length = 4096)
//        val photoByteArray: ByteArray?,

        var photoPermission: String?,

        var casPasswordOverride: String?,

        var modificationDate: Date,

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var basicData: BasicData,

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var contactData: ContactData,

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val additionalData: AdditionalData,

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val applicantForeignerData: ApplicantForeignerData?,

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val educationData: EducationData,

        var usosId: Long? = null,

        var assignedIndexNumber: String? = null,

        @JsonIgnore
        @OneToMany(mappedBy = "applicant")
        var applications: MutableList<Application> = mutableListOf()
) {

    override fun toString(): String {
        return "Applicant(id=$id, irkId=$irkId, email='$email', indexNumber=$indexNumber, " +
                "password='$password', name=$name, phone=$phone, citizenship=$citizenship, " +
                "photo=$photo, photoPermission=$photoPermission, casPasswordOverride=$casPasswordOverride, " +
                "modificationDate=$modificationDate, usosId=$usosId)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Applicant

        if (id != other.id) return false
        if (irkId != other.irkId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + irkId.hashCode()
        return result
    }


}