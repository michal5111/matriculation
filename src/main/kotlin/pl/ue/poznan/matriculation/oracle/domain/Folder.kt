package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import javax.persistence.*

@Entity
@Table(name = "DZ_TECZKI")
data class Folder(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_TECZ_SEQ")
        @SequenceGenerator(sequenceName = "DZ_TECZ_SEQ", allocationSize = 1, name = "DZ_TECZ_SEQ")
        @Column(name = "ID", length = 10)
        val id: Long? = null,

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = false)
        var person: Person,

        @Column(name = "SYGNATURA", length = 100, nullable = false)
        var signature: String,

        @Column(name = "MAGAZYN", length = 50, nullable = true)
        var warehouse: String? = null,

        @Column(name = "CZY_MATURA", length = 1, nullable = true)
        var containingSecondarySchoolCertificate: Char? = 'N',

        @Column(name = "CZY_METRYKA_BIBL", length = 1, nullable = true)
        var containingMetric: Char? = 'N',

        @Column(name = "CZY_INDEKS_RECEN", length = 1, nullable = true)
        var containingIndexOrReview: Char? = 'N',

        @Column(name = "UWAGI", length = 200, nullable = true)
        var comments: String? = null,

        @Column(name = "CZY_LEGITYMACJA", length = 1, nullable = false)
        var containingStudentCard: Char = 'N',

        @Column(name = "CZY_DYPLOM", length = 1, nullable = false)
        var containingDiploma: Char = 'N',

        @Column(name = "STATUS", length = 1, nullable = false)
        var status: Char = 'N',

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "code")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD", nullable = true)
        var organizationalUnit: OrganizationalUnit? = null,

        @Column(name = "LICZBA_TECZEK", length = 2, nullable = true)
        var foldersCount: Int? = null,

        @JsonIgnore
        @OneToMany(mappedBy = "folder", fetch = FetchType.LAZY)
        var personProgrammes: MutableList<PersonProgramme>
)