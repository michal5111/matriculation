package pl.ue.poznan.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import pl.ue.poznan.matriculation.local.domain.applications.Application
import java.util.*
import javax.persistence.*

@Entity
data class Applicant(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(unique = true)
        val irkId: Long,

        @Column(unique = true)
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

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        var basicData: BasicData,

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        var contactData: ContactData,

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        val additionalData: AdditionalData,

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        val applicantForeignerData: ApplicantForeignerData?,

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        val educationData: EducationData,

        var usosId: Long? = null,

        @JsonIgnore
        @LazyCollection(LazyCollectionOption.FALSE)
        @OneToMany(mappedBy = "applicant")
        var applications: MutableList<Application> = mutableListOf()
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Applicant

                if (id != other.id) return false
                if (irkId != other.irkId) return false
                if (email != other.email) return false
                if (indexNumber != other.indexNumber) return false
                if (password != other.password) return false
                if (name != other.name) return false
                if (phone != other.phone) return false
                if (citizenship != other.citizenship) return false
                if (photo != other.photo) return false
                if (photoPermission != other.photoPermission) return false
                if (casPasswordOverride != other.casPasswordOverride) return false
                if (modificationDate != other.modificationDate) return false
                if (basicData != other.basicData) return false
                if (contactData != other.contactData) return false
                if (additionalData != other.additionalData) return false
                if (applicantForeignerData != other.applicantForeignerData) return false
                if (educationData != other.educationData) return false
                if (usosId != other.usosId) return false

                return true
        }

        override fun hashCode(): Int {
                var result = id?.hashCode() ?: 0
                result = 31 * result + irkId.hashCode()
                result = 31 * result + email.hashCode()
                result = 31 * result + (indexNumber?.hashCode() ?: 0)
                result = 31 * result + password.hashCode()
                result = 31 * result + name.hashCode()
                result = 31 * result + (phone?.hashCode() ?: 0)
                result = 31 * result + (citizenship?.hashCode() ?: 0)
                result = 31 * result + (photo?.hashCode() ?: 0)
                result = 31 * result + (photoPermission?.hashCode() ?: 0)
                result = 31 * result + (casPasswordOverride?.hashCode() ?: 0)
                result = 31 * result + modificationDate.hashCode()
                result = 31 * result + basicData.hashCode()
                result = 31 * result + contactData.hashCode()
                result = 31 * result + additionalData.hashCode()
                result = 31 * result + (applicantForeignerData?.hashCode() ?: 0)
                result = 31 * result + educationData.hashCode()
                result = 31 * result + (usosId?.hashCode() ?: 0)
                return result
        }

        override fun toString(): String {
                return "Applicant(id=$id, irkId=$irkId, email='$email', indexNumber=$indexNumber, password='$password', name=$name, phone=$phone, citizenship=$citizenship, photo=$photo, photoPermission=$photoPermission, casPasswordOverride=$casPasswordOverride, modificationDate=$modificationDate, basicData=$basicData, contactData=$contactData, additionalData=$additionalData, applicantForeignerData=$applicantForeignerData, educationData=$educationData, usosId=$usosId)"
        }


}