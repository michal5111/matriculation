package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_TRYBY_ODBYWANIA_STUDIOW")
class SourceOfFinancing(
    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 100, nullable = false)
    val description: String,

    @Column(name = "KOD_GUS", length = 2, nullable = true)
    val gusCode: String? = null,

    @Column(name = "CZY_AKTUALNY", length = 1, nullable = false)
    val isCurrent: Char = 'T',

    @OneToMany(mappedBy = "sourceOfFinancing", fetch = FetchType.LAZY)
    val personProgrammeSourceOfFinancing: MutableList<PersonProgrammeSourceOfFinancing>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SourceOfFinancing

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}