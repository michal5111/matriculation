package pl.poznan.ue.matriculation.local.domain.applications


import org.hibernate.annotations.CacheConcurrencyStrategy
import pl.poznan.ue.matriculation.local.domain.Turn
import pl.poznan.ue.matriculation.local.domain.enum.ApplicationImportStatus
import pl.poznan.ue.matriculation.local.domain.import.Import
import java.io.Serializable
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

        @Enumerated(EnumType.STRING)
        var applicationImportStatus: ApplicationImportStatus = ApplicationImportStatus.NOT_IMPORTED,

        @Column(length = 4096)
        var importError: String? = null,

        @Lob
        var stackTrace: String? = null,

        @Transient
//        @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
//        @JoinColumns(
//                JoinColumn(name = "programme", referencedColumnName = "programme"),
//                JoinColumn(name = "registration", referencedColumnName = "registration"),
//                JoinColumn(name = "date_from", referencedColumnName = "date_from"),
//                JoinColumn(name = "date_to", referencedColumnName = "date_to")
//        )
        val turn: Turn,

        val irkInstance: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        var applicant: pl.poznan.ue.matriculation.local.domain.applicants.Applicant? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "import_id", referencedColumnName = "id")
        var import: Import? = null
): Serializable {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Application

                if (id != other.id) return false
                if (irkId != other.irkId) return false
                if (admitted != other.admitted) return false
                if (comment != other.comment) return false
                if (payment != other.payment) return false
                if (position != other.position) return false
                if (qualified != other.qualified) return false
                if (score != other.score) return false
                if (importError != other.importError) return false
                if (stackTrace != other.stackTrace) return false
                if (irkInstance != other.irkInstance) return false

                return true
        }

        override fun hashCode(): Int {
                var result = id?.hashCode() ?: 0
                result = 31 * result + irkId.hashCode()
                result = 31 * result + (admitted?.hashCode() ?: 0)
                result = 31 * result + (comment?.hashCode() ?: 0)
                result = 31 * result + (payment?.hashCode() ?: 0)
                result = 31 * result + (position?.hashCode() ?: 0)
                result = 31 * result + (qualified?.hashCode() ?: 0)
                result = 31 * result + (score?.hashCode() ?: 0)
                result = 31 * result + (importError?.hashCode() ?: 0)
                result = 31 * result + (stackTrace?.hashCode() ?: 0)
                result = 31 * result + irkInstance.hashCode()
                return result
        }

        override fun toString(): String {
                return "Application(id=$id, irkId=$irkId, admitted=$admitted, comment=$comment, payment=$payment, position=$position, qualified=$qualified, score=$score, importError=$importError, stackTrace=$stackTrace, irkInstance='$irkInstance')"
        }


}