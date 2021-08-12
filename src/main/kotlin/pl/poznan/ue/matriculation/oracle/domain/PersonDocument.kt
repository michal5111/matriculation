package pl.poznan.ue.matriculation.oracle.domain

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "DZ_DOKUMENTY_OSOB")
class PersonDocument(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_DOKOS_SEQ")
    @SequenceGenerator(sequenceName = "DZ_DOKOS_SEQ", allocationSize = 1, name = "DZ_DOKOS_SEQ")
    @Column(name = "ID", length = 10)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = false)
    var person: Person,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TDOK_KOD", referencedColumnName = "KOD", nullable = false)
    var documentType: DocumentType,

    @Column(name = "SYGNATURA", length = 100, nullable = false)
    var signature: String,

    @Column(name = "DATA_WYDANIA", nullable = false)
    var issueDate: Date,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JZK_KOD", referencedColumnName = "KOD", nullable = true)
    var language: Language? = null,

    @Column(name = "OPIS", length = 1000, nullable = true)
    var description: String? = null,

    @Lob
    @Column(name = "DOKUMENT", nullable = true)
    var document: ByteArray? = null,

    @Column(name = "DATA_POTW_ODBIORU", nullable = true)
    var receiptDate: Date,

    @Column(name = "DOKUMENT_ID_BLOBBOX", length = 20, nullable = true)
    var documentBlobboxId: String? = null,


    @Column(name = "WLASCICIEL", length = 1, nullable = false)
    var owner: Char,

    @Column(name = "FORMAT", length = 4, nullable = false)
    var format: String,

    @OneToMany(mappedBy = "personDocument")
    var arrivals: MutableList<Arrival>

    //Todo Add ELS_CERT_ID
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonDocument

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}