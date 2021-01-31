package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.poznan.ue.matriculation.local.domain.applications.Application
import java.util.*
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "ForeignIdUniqueConstraint", columnNames = ["foreignId", "datasourceId"])]
//        indexes = [
//            Index(name = "foreignIdDatasourceIdIndex", columnList = "foreignId,datasourceId", unique = true),
//            Index(name = "usosIdIndex", columnList = "usosId", unique = true)
//        ]
)
class Applicant(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(name = "foreignId")
        val foreignId: Long,

        @Column(name = "datasourceId", nullable = false)
        var dataSourceId: String? = null,

        var email: String,

        var indexNumber: String?,

        var password: String?,

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        val name: Name,

        var phone: String?,

        var citizenship: String,

        var nationality: String? = null,

        var photo: String?,

        @Transient
        @JsonIgnore
        var photoByteArray: ByteArray? = null,

        var photoPermission: String?,

        var casPasswordOverride: String?,

        var modificationDate: Date,

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        var basicData: BasicData,

//        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
//        var contactData: ContactData,

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        val additionalData: AdditionalData,

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var applicantForeignerData: ApplicantForeignerData?,

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        val educationData: EducationData,

        @OneToMany(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
        val phoneNumbers: MutableList<PhoneNumber> = mutableListOf(),

        @OneToMany(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
        val addresses: MutableList<Address> = mutableListOf(),

        @OneToMany(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
        var identityDocuments: MutableList<IdentityDocument> = mutableListOf(),

        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
        var erasmusData: ErasmusData? = null,

        var usosId: Long? = null,

        var assignedIndexNumber: String? = null,

        @JsonIgnore
        @OneToMany(mappedBy = "applicant")
        var applications: MutableSet<Application> = mutableSetOf(),

        var uid: String? = null
) {

    override fun toString(): String {
        return "Applicant(id=$id, irkId=$foreignId, email='$email', indexNumber=$indexNumber, " +
                "password='$password', name=$name, phone=$phone, citizenship=$citizenship, " +
                "photo=$photo, photoPermission=$photoPermission, casPasswordOverride=$casPasswordOverride, " +
                "modificationDate=$modificationDate, usosId=$usosId)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Applicant

        if (foreignId != other.foreignId) return false
        if (dataSourceId != other.dataSourceId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = foreignId.hashCode()
        result = 31 * result + (dataSourceId?.hashCode() ?: 0)
        return result
    }


}