package pl.ue.poznan.matriculation.local.domain.import

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.ue.poznan.matriculation.local.domain.enum.ImportStatus
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
)

