package pl.poznan.ue.matriculation.local.domain.import

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import java.io.Serializable
import javax.persistence.*

@Entity
class ImportProgress(
        @Id
        var id: Long? = null,

        @MapsId
        @OneToOne(fetch = FetchType.LAZY)
        @JsonIgnore
        @JoinColumn(name = "import_id", referencedColumnName = "id")
        var import: Import,

        var importedApplications: Int = 0,

        var saveErrors: Int = 0,

        var savedApplicants: Int = 0,

        var totalCount: Int? = null,

        @Enumerated(EnumType.STRING)
        var importStatus: ImportStatus = ImportStatus.PENDING,

        @Lob
        var error: String? = null
) : Serializable {

    override fun toString(): String {
        return "ImportProgress(id=$id, importedApplications=$importedApplications, saveErrors=$saveErrors, " +
                "savedApplicants=$savedApplicants, totalCount=$totalCount, importStatus=$importStatus, error=$error)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImportProgress

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }


}

