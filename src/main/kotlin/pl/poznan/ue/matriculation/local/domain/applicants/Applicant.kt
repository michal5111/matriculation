package pl.poznan.ue.matriculation.local.domain.applicants

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.DuplicateStatus
import java.util.*
import java.util.concurrent.Future

@NamedEntityGraphs(
    NamedEntityGraph(
        name = "applicant.data",
        attributeNodes = [
            NamedAttributeNode("applicantForeignerData"),
            NamedAttributeNode("documents")
        ]
    ),
    NamedEntityGraph(
        name = "applicant.identityDocuments",
        attributeNodes = [
            NamedAttributeNode("identityDocuments")
        ]
    )
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

    @Column(name = "foreignId", nullable = false)
    val foreignId: Long,

    @Column(name = "datasourceId", nullable = false)
    var dataSourceId: String? = null,

    @Email
    var email: String,

    var indexNumber: String? = null,

    var password: String? = null,

    var citizenship: String? = null,

    var nationality: String? = null,

    var photo: String? = null,

    @Transient
    var photoByteArrayFuture: Future<ByteArray?>? = null,

    var photoPermission: String? = null,

    var modificationDate: Date? = null,

    @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var applicantForeignerData: ApplicantForeignerData? = null,

    @OneToMany(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val phoneNumbers: MutableSet<PhoneNumber> = HashSet(),

    @OneToMany(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val addresses: MutableSet<Address> = HashSet(),

    @OneToMany(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val identityDocuments: MutableSet<IdentityDocument> = HashSet(),

    @OneToOne(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var erasmusData: ErasmusData? = null,

    var usosId: Long? = null,

    var assignedIndexNumber: String? = null,

    @Enumerated(EnumType.STRING)
    var potentialDuplicateStatus: DuplicateStatus = DuplicateStatus.NOT_CHECKED,

    @OneToMany(mappedBy = "applicant", fetch = FetchType.LAZY)
    var applications: MutableSet<Application> = HashSet(),

    var uid: String? = null,

    var fathersName: String? = null,

    var militaryCategory: String? = null,

    var militaryStatus: String? = null,

    var mothersName: String? = null,

    var wku: String? = null,

    var sex: Char,

    var pesel: String? = null,

    @Temporal(TemporalType.DATE)
    var dateOfBirth: Date?,

    var cityOfBirth: String? = null,

    var countryOfBirth: String? = null,

    @OneToMany(mappedBy = "applicant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val documents: MutableSet<Document> = HashSet(),

    var highSchoolCity: String? = null,

    var highSchoolName: String? = null,

    var highSchoolType: String? = null,

    var highSchoolUsosCode: Long? = null,

    var middle: String? = null,

    var family: String,

    var given: String,

    var maiden: String? = null,

    var personExisted: Boolean = false,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_id_document_id", referencedColumnName = "id")
    var primaryIdentityDocument: IdentityDocument? = null
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

    fun addDocument(document: Document) {
        documents.add(document)
        document.applicant = this
    }

    fun removeDocument(document: Document) {
        documents.remove(document)
        document.applicant = null
    }

    override fun toString(): String {
        return "Applicant(" +
            "foreignId=$foreignId, " +
            "dataSourceId=$dataSourceId, " +
            "email='$email', " +
            "indexNumber=$indexNumber, " +
            "citizenship='$citizenship', " +
            "nationality=$nationality, " +
            "photo=$photo, " +
            "photoPermission=$photoPermission, " +
            "usosId=$usosId, " +
            "assignedIndexNumber=$assignedIndexNumber, " +
            "uid=$uid, " +
            "fathersName=$fathersName, " +
            "militaryCategory=$militaryCategory, " +
            "militaryStatus=$militaryStatus, " +
            "mothersName=$mothersName, " +
            "wku=$wku, " +
            "sex=$sex, " +
            "pesel=$pesel, " +
            "dateOfBirth=$dateOfBirth, " +
            "cityOfBirth=$cityOfBirth, " +
            "countryOfBirth=$countryOfBirth, " +
            "highSchoolCity=$highSchoolCity, " +
            "highSchoolName=$highSchoolName, " +
            "highSchoolType=$highSchoolType, " +
            "highSchoolUsosCode=$highSchoolUsosCode, " +
            "middle=$middle, " +
            "family='$family', " +
            "given='$given', " +
            "maiden=$maiden)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Applicant) return false

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
