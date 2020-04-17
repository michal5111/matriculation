package pl.ue.poznan.matriculation.oracle.domain

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "DZ_TYPY_INDEKSOW")
data class IndexType(
        @Id
        @NotBlank
        @Column(name = "KOD", length = 20, nullable = false)
        val code: String,

        @Column(name = "OPIS", length = 100, nullable = false)
        var description: String,

        @Column(name = "CZY_LICZBOWY", length = 1, nullable = false)
        var isNumeric: Char = 'N',

        @Column(name = "CZY_UNIKATOWY", length = 1, nullable = false)
        var isUnique: Char = 'N',

        @Column(name = "CZY_Z_PREFIKSEM", length = 1, nullable = false)
        var isWithPrefix: Char = 'N',

        @Column(name = "CZY_PULA_CENTRALNA", length = 1, nullable = false)
        var isCentralPool: Char = 'N',

        @Column(name = "CZY_AKTUALNY", length = 1, nullable = false)
        var isCurrent: Char = 'T',

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "indexType")
        var students: MutableList<Student>
)