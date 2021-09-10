package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.Hibernate
import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.DuplicateStatus
import java.util.*
import java.util.concurrent.Future
import javax.persistence.*

@NamedEntityGraph(
    name = "applicant.data",
    attributeNodes = [
        NamedAttributeNode("name"),
        NamedAttributeNode("basicData"),
        NamedAttributeNode("additionalData"),
        NamedAttributeNode("educationData"),
        NamedAttributeNode("applicantForeignerData"),
        NamedAttributeNode("educationData", subgraph = "subgraph.documents")
    ],
    subgraphs = [
        NamedSubgraph(
            name = "subgraph.documents",
            attributeNodes = [
                NamedAttributeNode("documents")
            ]
        )
    ]
)
@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(
        name = "ForeignIdUniqueConstraint",
        columnNames = ["foreignId", "datasourceId"]
    )],
    indexes = [
        Index(name = "foreignIdDatasourceIdIndex", columnList = "foreignId,datasourceId", unique = true),
        Index(name = "usosIdIndex", columnList = "usosId", unique = true)
    ]
)
class Applicant(

    @Column(name = "foreignId")
    val foreignId: Long,

    @Column(name = "datasourceId", nullable = false)
    var dataSourceId: String? = null,

    var email: String,

    var indexNumber: String?,

    var password: String?,

    @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val name: Name,

    var citizenship: String,

    var nationality: String? = null,

    var photo: String?,

    @Transient
    @JsonIgnore
    var photoByteArrayFuture: Future<ByteArray?>? = null,

    var photoPermission: String?,

    var modificationDate: Date,

    @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var basicData: BasicData,

//        @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
//        var contactData: ContactData,

    @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val additionalData: AdditionalData,

    @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var applicantForeignerData: ApplicantForeignerData?,

    @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val educationData: EducationData,

    @OneToMany(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val phoneNumbers: MutableSet<PhoneNumber> = HashSet(),

    @OneToMany(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val addresses: MutableSet<Address> = HashSet(),

    @OneToMany(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var identityDocuments: MutableSet<IdentityDocument> = HashSet(),

    @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var erasmusData: ErasmusData? = null,

    var usosId: Long? = null,

    var assignedIndexNumber: String? = null,

    @Enumerated(EnumType.STRING)
    var potentialDuplicateStatus: DuplicateStatus = DuplicateStatus.NOT_CHECKED,

    @JsonIgnore
    @OneToMany(mappedBy = "applicant", fetch = FetchType.LAZY)
    var applications: MutableSet<Application> = HashSet(),

    var uid: String? = null
) : BaseEntityLongId() {

    fun addPhoneNumber(phoneNumber: PhoneNumber) {
        phoneNumbers.add(phoneNumber)
        phoneNumber.applicant = this
    }

    fun removePhoneNumber(phoneNumber: PhoneNumber) {
        phoneNumbers.remove(phoneNumber)
        phoneNumber.applicant = null
    }

    fun addAddress(address: Address) {
        addresses.add(address)
        address.applicant = this
    }

    fun removeAddress(address: Address) {
        addresses.remove(address)
        address.applicant = this
    }

    fun addIdentityDocument(identityDocument: IdentityDocument) {
        identityDocuments.add(identityDocument)
        identityDocument.applicant = this
    }

    fun removeIdentityDocument(identityDocument: IdentityDocument) {
        identityDocuments.remove(identityDocument)
        identityDocument.applicant = null
    }

    override fun toString(): String {
        return "Applicant(id=$id, irkId=$foreignId, email='$email', indexNumber=$indexNumber, " +
            "password='$password', name=$name, citizenship=$citizenship, " +
            "photo=$photo, photoPermission=$photoPermission, " +
            "modificationDate=$modificationDate, usosId=$usosId)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Applicant

        return id != null && id == other.id
    }

    override fun hashCode(): Int = 0
}
