package pl.poznan.ue.matriculation.local.domain.import

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import pl.poznan.ue.matriculation.local.domain.applications.Application
import java.util.*
import javax.persistence.*

@Entity
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

    @Lob
    val dataFile: ByteArray?,

    @JsonIgnore
    @OneToMany(mappedBy = "import", fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    val applications: MutableSet<Application> = mutableSetOf()
) : BaseEntityLongId() {

    @OneToOne(mappedBy = "import", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.REMOVE])
    var importProgress: ImportProgress = ImportProgress(import = this)

    override fun toString(): String {
        return "Import(id=$id, programmeCode='$programmeCode', programmeForeignId='$programmeForeignId', stageCode='$stageCode', registration='$registration', indexPoolCode='$indexPoolCode', startDate=$startDate, dateOfAddmision=$dateOfAddmision, didacticCycleCode='$didacticCycleCode', dataSourceId='$dataSourceId')"
    }

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
        result = 31 * result + (dataFile?.contentHashCode() ?: 0)
        return result
    }


}