package pl.poznan.ue.matriculation.local.domain.applications


import com.fasterxml.jackson.annotation.*
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "ForeignIdUniqueConstraint", columnNames = ["foreignId", "datasourceId"])]
//        indexes = [
//            Index(name = "foreignIdDatasourceIdIndex", columnList = "foreignId,datasourceId", unique = true),
//            Index(name = "importIdIndex", columnList = "import_id", unique = false),
//            Index(name = "importIdImportStatusIndex", columnList = "import_id,importStatus", unique = false)
//        ]
)
class Application(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, name = "foreignId")
    val foreignId: Long,

    @Column(nullable = false, name = "datasourceId")
    var dataSourceId: String? = null,

    var admitted: String? = null,

    @Column(length = 500)
    var comment: String? = null,

    @OneToOne(mappedBy = "application", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val applicationForeignerData: ApplicationForeignerData? = null,

    var payment: String? = null,

    var position: String? = null,

    var qualified: String? = null,

    var score: String? = null,

    var editUrl: String? = null,

    @JsonProperty("certificateId")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificate_id", referencedColumnName = "id")
    var certificate: Document? = null,

    @Enumerated(EnumType.STRING)
        var importStatus: ApplicationImportStatus = ApplicationImportStatus.NOT_IMPORTED,

    @Column(length = 4096)
        var importError: String? = null,

    @Lob
        var stackTrace: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH])
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        var applicant: Applicant? = null,

    @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "import_id", referencedColumnName = "id")
        var import: Import? = null
) : Serializable {

    override fun toString(): String {
        return "Application(id=$id, irkId=$foreignId, admitted=$admitted, comment=$comment, payment=$payment, " +
                "position=$position, qualified=$qualified, score=$score, importError=$importError, " +
                "stackTrace=$stackTrace)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Application

        if (id != other.id) return false
        if (foreignId != other.foreignId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + foreignId.hashCode()
        return result
    }


}