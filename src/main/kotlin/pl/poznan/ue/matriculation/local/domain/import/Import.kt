package pl.poznan.ue.matriculation.local.domain.import

import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Type
import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import pl.poznan.ue.matriculation.local.entityListeners.MessageAfterUpdateListener
import java.util.*
import javax.persistence.*

@Entity
@EntityListeners(MessageAfterUpdateListener::class)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "import.error",
        attributeNodes = [
            NamedAttributeNode("error")
        ]
    ),
    NamedEntityGraph(
        name = "import.importStatus",
        attributeNodes = [
            NamedAttributeNode("importStatus")
        ]
    ),
    NamedEntityGraph(
        name = "import.all",
        attributeNodes = [
            NamedAttributeNode("importStatus"),
            NamedAttributeNode("error"),
            NamedAttributeNode("dataSourceId")
        ]
    )
)
class Import(
    val programmeCode: String,

    val programmeForeignId: String,

    val programmeForeignName: String,

    val stageCode: String,

    val registration: String,

    val indexPoolCode: String?,

    val indexPoolName: String?,

    @Temporal(TemporalType.DATE)
    val startDate: Date,

    @Temporal(TemporalType.DATE)
    val dateOfAddmision: Date,

    val didacticCycleCode: String,

    val dataSourceId: String,

    val dataSourceName: String,

    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonType")
    @Column(columnDefinition = "json")
    val additionalProperties: Map<String, Any>?,

    @Volatile
    var importedApplications: Int = 0,

    @Volatile
    var saveErrors: Int = 0,

    @Volatile
    var savedApplicants: Int = 0,

    @Volatile
    var totalCount: Int? = null,

    @Volatile
    var importedUids: Int = 0,

    @Volatile
    var notificationsSend: Int = 0,

    @Volatile
    var potentialDuplicates: Int = 0,

    @Volatile
    @Enumerated(EnumType.STRING)
    var importStatus: ImportStatus = ImportStatus.PENDING,

    @Volatile
    @Lob
    var error: String? = null,

    @Lob
    var stackTrace: String? = null,

    @OneToMany(mappedBy = "import", fetch = FetchType.LAZY, cascade = [CascadeType.MERGE, CascadeType.REMOVE])
    val applications: MutableSet<Application> = HashSet()
) : BaseEntityLongId() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Import) return false

        if (programmeCode != other.programmeCode) return false
        if (programmeForeignId != other.programmeForeignId) return false
        if (programmeForeignName != other.programmeForeignName) return false
        if (stageCode != other.stageCode) return false
        if (registration != other.registration) return false
        if (indexPoolCode != other.indexPoolCode) return false
        if (indexPoolName != other.indexPoolName) return false
        if (startDate != other.startDate) return false
        if (dateOfAddmision != other.dateOfAddmision) return false
        if (didacticCycleCode != other.didacticCycleCode) return false
        if (dataSourceId != other.dataSourceId) return false
        if (additionalProperties != other.additionalProperties) return false
        if (importedApplications != other.importedApplications) return false
        if (saveErrors != other.saveErrors) return false
        if (savedApplicants != other.savedApplicants) return false
        if (totalCount != other.totalCount) return false
        if (importedUids != other.importedUids) return false
        if (notificationsSend != other.notificationsSend) return false
        if (potentialDuplicates != other.potentialDuplicates) return false
        if (importStatus != other.importStatus) return false
        if (error != other.error) return false
        if (stackTrace != other.stackTrace) return false

        return true
    }

    override fun hashCode(): Int {
        var result = programmeCode.hashCode()
        result = 31 * result + programmeForeignId.hashCode()
        result = 31 * result + programmeForeignName.hashCode()
        result = 31 * result + stageCode.hashCode()
        result = 31 * result + registration.hashCode()
        result = 31 * result + indexPoolCode.hashCode()
        result = 31 * result + indexPoolName.hashCode()
        result = 31 * result + startDate.hashCode()
        result = 31 * result + dateOfAddmision.hashCode()
        result = 31 * result + didacticCycleCode.hashCode()
        result = 31 * result + dataSourceId.hashCode()
        result = 31 * result + (additionalProperties?.hashCode() ?: 0)
        result = 31 * result + importedApplications
        result = 31 * result + saveErrors
        result = 31 * result + savedApplicants
        result = 31 * result + (totalCount ?: 0)
        result = 31 * result + importedUids
        result = 31 * result + notificationsSend
        result = 31 * result + potentialDuplicates
        result = 31 * result + importStatus.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        result = 31 * result + (stackTrace?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Import(" +
            "programmeCode='$programmeCode', " +
            "programmeForeignId='$programmeForeignId', " +
            "stageCode='$stageCode', " +
            "registration='$registration', " +
            "indexPoolCode='$indexPoolCode', " +
            "startDate=$startDate, " +
            "dateOfAddmision=$dateOfAddmision, " +
            "didacticCycleCode='$didacticCycleCode', " +
            "dataSourceId='$dataSourceId', " +
            "additionalProperties=$additionalProperties, " +
            "importedApplications=$importedApplications, " +
            "saveErrors=$saveErrors, " +
            "savedApplicants=$savedApplicants, " +
            "totalCount=$totalCount, " +
            "importedUids=$importedUids, " +
            "notificationsSend=$notificationsSend, " +
            "potentialDuplicates=$potentialDuplicates, " +
            "importStatus=$importStatus, " +
            "error=$error)"
    }
}
