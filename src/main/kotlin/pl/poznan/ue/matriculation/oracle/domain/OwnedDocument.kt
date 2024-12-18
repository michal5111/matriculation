package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "DZ_DOKUMENTY_POSIADANE")
class OwnedDocument(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_DOK_POS_SEQ")
    @SequenceGenerator(sequenceName = "DZ_DOK_POS_SEQ", allocationSize = 1, name = "DZ_DOK_POS_SEQ")
    @Column(name = "ID", length = 10)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = false)
    var person: Person,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TDOK_KOD", referencedColumnName = "KOD", nullable = false)
    var documentType: DocumentType,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_WYDANIA", nullable = true)
    var issueDate: LocalDate? = null,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_WAZNOSCI", nullable = true)
    var expirationDate: LocalDate? = null,

    @Column(name = "NUMER", length = 50, nullable = true)
    var number: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KRAJ_WYDANIA", referencedColumnName = "KOD")
    var issueCountry: Citizenship? = null
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OwnedDocument

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "OwnedDocument(id=$id, personId=${person.id}, documentTypeCode=${documentType.code}, issueDate=$issueDate, expirationDate=$expirationDate, number=$number, issueCountry=${issueCountry?.code})"
    }


}
