package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_TYPY_DOKUMENTOW")
class DocumentType(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 200, nullable = false)
    var description: String,

    @Column(name = "CZY_AKTUALNY", length = 1, nullable = false)
    var isCurrent: Char = 'T',

    @OneToMany(mappedBy = "documentType", fetch = FetchType.LAZY)
    var personDocuments: MutableList<PersonDocument>,

    @OneToMany(mappedBy = "documentType", fetch = FetchType.LAZY)
    var ownedDocuments: MutableList<OwnedDocument>,
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DocumentType

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}