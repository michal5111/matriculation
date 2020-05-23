package pl.poznan.ue.matriculation.local.domain.import

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus
import java.io.Serializable
import javax.persistence.*

@Entity
data class ImportProgress(
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
): Serializable {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as ImportProgress

                if (id != other.id) return false
                if (importedApplications != other.importedApplications) return false
                if (saveErrors != other.saveErrors) return false
                if (savedApplicants != other.savedApplicants) return false
                if (totalCount != other.totalCount) return false
                if (importStatus != other.importStatus) return false
                if (error != other.error) return false

                return true
        }

        override fun hashCode(): Int {
                var result = id?.hashCode() ?: 0
                result = 31 * result + importedApplications
                result = 31 * result + saveErrors
                result = 31 * result + savedApplicants
                result = 31 * result + (totalCount ?: 0)
                result = 31 * result + importStatus.hashCode()
                result = 31 * result + (error?.hashCode() ?: 0)
                return result
        }

        override fun toString(): String {
                return "ImportProgress(id=$id, importedApplications=$importedApplications, saveErrors=$saveErrors, savedApplicants=$savedApplicants, totalCount=$totalCount, importStatus=$importStatus, error=$error)"
        }


}

