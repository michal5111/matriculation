package pl.ue.poznan.matriculation.local.domain.import

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.ue.poznan.matriculation.local.domain.applications.Application
import java.util.*
import javax.persistence.*

@Entity
data class Import(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        val programmeCode: String,

        val stageCode: String,

        val registration: String,

        val indexPoolCode: String,

        val startDate: Date,

        val dateOfAddmision: Date,

        val didacticCycleCode: String,

        @JsonIgnore
        @OneToMany(mappedBy = "import", fetch = FetchType.EAGER, cascade = [CascadeType.MERGE])
        val applications: MutableList<Application> = mutableListOf(),

        @OneToOne(mappedBy = "import", fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
        var importProgress: ImportProgress? = null
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Import

                if (id != other.id) return false
                if (programmeCode != other.programmeCode) return false
                if (stageCode != other.stageCode) return false
                if (registration != other.registration) return false
                if (indexPoolCode != other.indexPoolCode) return false
                if (startDate != other.startDate) return false
                if (dateOfAddmision != other.dateOfAddmision) return false
                if (didacticCycleCode != other.didacticCycleCode) return false

                return true
        }

        override fun hashCode(): Int {
                var result = id?.hashCode() ?: 0
                result = 31 * result + programmeCode.hashCode()
                result = 31 * result + stageCode.hashCode()
                result = 31 * result + registration.hashCode()
                result = 31 * result + indexPoolCode.hashCode()
                result = 31 * result + startDate.hashCode()
                result = 31 * result + dateOfAddmision.hashCode()
                result = 31 * result + didacticCycleCode.hashCode()
                return result
        }

        override fun toString(): String {
                return "Import(id=$id, programmeCode='$programmeCode', stageCode='$stageCode', registration='$registration', indexPoolCode='$indexPoolCode', startDate=$startDate, dateOfAddmision=$dateOfAddmision, didacticCycleCode='$didacticCycleCode')"
        }


}