package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "DZ_OSOBY_HISTORIA_ZMIAN")
class PersonChangeHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_HIS_OS_SEQ")
    @SequenceGenerator(sequenceName = "DZ_HIS_OS_SEQ", allocationSize = 1, name = "DZ_HIS_OS_SEQ")
    @Column(name = "ID", nullable = false, updatable = false, length = 10)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = false, unique = false)
    var person: Person?,

    @Column(name = "PESEL", length = 11, nullable = true)
    var pesel: String? = null,

    @Column(name = "IMIE", length = 40, nullable = true)
    var name: String? = null,

    @Column(name = "IMIE2", length = 40, nullable = true)
    var middleName: String? = null,

    @Column(name = "NAZWISKO", length = 40, nullable = true)
    var surname: String? = null,

    @Column(name = "SR_NR_DOWODU", length = 20, nullable = true)
    var idNumber: String? = null,

    @Column(name = "NIP", length = 13, nullable = true)
    val nip: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NAR_KOD", referencedColumnName = "KOD")
    var nationality: Citizenship? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OB_KOD", referencedColumnName = "KOD")
    var citizenship: Citizenship? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TDOK_KOD", referencedColumnName = "KOD", nullable = true)
    var identityDocumentType: DocumentType? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KRAJ_DOK_KOD", referencedColumnName = "KOD", nullable = true)
    val identityDocumentIssuerCountry: Citizenship? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "US_ID", referencedColumnName = "ID", nullable = true)
    val taxOffice: TaxOffice? = null,

    @Column(name = "KOMENTARZ", length = 1000, nullable = true)
    var comment: String? = "Dane zmienione w trakcie immatrykulacji przez Immatrykulator 5000",

    @Column(name = "DATA_ZMIANY", nullable = false)
    var changeDate: Date = Date(),

    @Column(name = "PLEC", nullable = true, length = 1)
    var sex: Char? = null,

    @Column(name = "TYP", nullable = false)
    var type: String = "B"
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonChangeHistory

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}

