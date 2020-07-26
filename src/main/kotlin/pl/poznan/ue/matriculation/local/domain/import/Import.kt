package pl.poznan.ue.matriculation.local.domain.import

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.poznan.ue.matriculation.local.domain.applications.Application
import java.util.*
import javax.persistence.*

@Entity
class Import(
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

        val dataSourceId: String,

        @JsonIgnore
        @OneToMany(mappedBy = "import", fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
        val applications: MutableList<Application> = mutableListOf(),

        @OneToOne(mappedBy = "import", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.REMOVE])
        var importProgress: ImportProgress? = null
) {

    override fun toString(): String {
        return "Import(id=$id, programmeCode='$programmeCode', stageCode='$stageCode', " +
                "registration='$registration', indexPoolCode='$indexPoolCode', " +
                "startDate=$startDate, dateOfAddmision=$dateOfAddmision, " +
                "didacticCycleCode='$didacticCycleCode')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Import

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }


}