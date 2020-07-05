package pl.poznan.ue.matriculation.local.domain.applications


import com.fasterxml.jackson.annotation.JsonIgnore
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import java.io.Serializable
import javax.persistence.*

@Entity
class Application(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(unique = true)
        val irkId: Long,

        var admitted: String?,

        @Column(length = 500)
        var comment: String?,

        @OneToOne(mappedBy = "application", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        val applicationForeignerData: ApplicationForeignerData?,

        var payment: String?,

        var position: String?,

        var qualified: String?,

        var score: String?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "certificate_id", referencedColumnName = "id")
        var certificate: Document? = null,

        @Enumerated(EnumType.STRING)
        var importStatus: ApplicationImportStatus = ApplicationImportStatus.NOT_IMPORTED,

        @Column(length = 4096)
        var importError: String? = null,

        @Lob
        var stackTrace: String? = null,

        val irkInstance: String,

        @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH])
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        var applicant: Applicant? = null,

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "import_id", referencedColumnName = "id")
        var import: Import? = null
) : Serializable {

    override fun toString(): String {
        return "Application(id=$id, irkId=$irkId, admitted=$admitted, comment=$comment, payment=$payment, " +
                "position=$position, qualified=$qualified, score=$score, importError=$importError, " +
                "stackTrace=$stackTrace, irkInstance='$irkInstance')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Application

        if (id != other.id) return false
        if (irkId != other.irkId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + irkId.hashCode()
        return result
    }


}