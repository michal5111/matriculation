package pl.poznan.ue.matriculation.local.domain.applications


import com.fasterxml.jackson.annotation.JsonIgnore
import pl.poznan.ue.matriculation.local.domain.BaseEntityLongId
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import java.io.Serializable
import javax.persistence.*

@NamedEntityGraph(
    name = "application.applicant",
    attributeNodes = [
        NamedAttributeNode("applicant", subgraph = "subgraph.data"),
        NamedAttributeNode("certificate")
    ],
    subgraphs = [
        NamedSubgraph(
            name = "subgraph.data",
            attributeNodes = [
                NamedAttributeNode("primaryIdentityDocument"),
                NamedAttributeNode("erasmusData")
            ]
        )
    ]
)
@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(name = "ForeignIdUniqueConstraint", columnNames = ["foreignId", "datasourceId"])],
    indexes = [
        Index(name = "foreignIdDatasourceIdIndex", columnList = "foreignId,datasourceId", unique = true),
        Index(name = "importIdIndex", columnList = "import_id", unique = false),
        Index(name = "importIdImportStatusIndex", columnList = "import_id,importStatus", unique = false)
    ]
)
class Application(

    @Column(nullable = false, name = "foreignId")
    val foreignId: Long,

    @Column(nullable = false, name = "datasourceId")
    var dataSourceId: String? = null,

    var editUrl: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificate_id", referencedColumnName = "id")
    var certificate: Document? = null,

    @Enumerated(EnumType.STRING)
    var importStatus: ApplicationImportStatus = ApplicationImportStatus.NOT_IMPORTED,

    @Column(length = 4096)
    var importError: String? = null,

    @Lob
    var stackTrace: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    var applicant: Applicant? = null,

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_id", referencedColumnName = "id")
    var import: Import? = null,

    var baseOfStay: String? = null,

    var basisOfAdmission: String? = null,

    var sourceOfFinancing: String? = null,

    var notificationSent: Boolean = false,

    @Lob
    var warnings: String? = null
) : BaseEntityLongId(), Serializable {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Application

        if (foreignId != other.foreignId) return false
        if (dataSourceId != other.dataSourceId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = foreignId.hashCode()
        result = 31 * result + (dataSourceId?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Application(foreignId=$foreignId, dataSourceId=$dataSourceId, editUrl=$editUrl, importStatus=$importStatus, importError=$importError, stackTrace=$stackTrace, baseOfStay=$baseOfStay, basisOfAdmission=$basisOfAdmission, sourceOfFinancing=$sourceOfFinancing, notificationSent=$notificationSent, warnings=$warnings)"
    }
}
