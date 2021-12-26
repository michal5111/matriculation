package pl.poznan.ue.matriculation.local.domain.import

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CacheConcurrencyStrategy
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

    val stageCode: String,

    val registration: String,

    val indexPoolCode: String,

    val startDate: Date,

    val dateOfAddmision: Date,

    val didacticCycleCode: String,

    val dataSourceId: String,

    @get:JsonIgnore
    @Lob
    val dataFile: ByteArray?,

    var importedApplications: Int = 0,

    var saveErrors: Int = 0,

    var savedApplicants: Int = 0,

    var totalCount: Int? = null,

    var importedUids: Int = 0,

    var notificationsSend: Int = 0,

    var potentialDuplicates: Int = 0,

    @Enumerated(EnumType.STRING)
    var importStatus: ImportStatus = ImportStatus.PENDING,

    @Lob
    var error: String? = null,

    @JsonIgnore
    @OneToMany(mappedBy = "import", fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    val applications: MutableSet<Application> = HashSet()
) : BaseEntityLongId() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Import

        if (programmeCode != other.programmeCode) return false
        if (programmeForeignId != other.programmeForeignId) return false
        if (stageCode != other.stageCode) return false
        if (registration != other.registration) return false
        if (indexPoolCode != other.indexPoolCode) return false
        if (startDate != other.startDate) return false
        if (dateOfAddmision != other.dateOfAddmision) return false
        if (didacticCycleCode != other.didacticCycleCode) return false
        if (dataSourceId != other.dataSourceId) return false
        if (dataFile != null) {
            if (other.dataFile == null) return false
            if (!dataFile.contentEquals(other.dataFile)) return false
        } else if (other.dataFile != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = programmeCode.hashCode()
        result = 31 * result + programmeForeignId.hashCode()
        result = 31 * result + stageCode.hashCode()
        result = 31 * result + registration.hashCode()
        result = 31 * result + indexPoolCode.hashCode()
        result = 31 * result + startDate.hashCode()
        result = 31 * result + dateOfAddmision.hashCode()
        result = 31 * result + didacticCycleCode.hashCode()
        result = 31 * result + dataSourceId.hashCode()
        result = 31 * result + (dataFile?.hashCode() ?: 0)
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
            "dataFile=${dataFile?.contentToString()}, " +
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
