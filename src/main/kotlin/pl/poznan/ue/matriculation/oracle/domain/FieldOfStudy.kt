package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_KIERUNKI_STUDIOW")
class FieldOfStudy(

    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 200, nullable = false)
    val description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NADRZEDNY_KOD", referencedColumnName = "KOD", nullable = false)
    var fieldOfStudy: FieldOfStudy? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYP_KIERUNKU_KOD", referencedColumnName = "KOD", nullable = false)
    var fieldOfStudyType: FieldOfStudyType,

    @Column(name = "CZY_WYSWIETLAC", length = 1, nullable = false)
    var display: Char = 'T',

    @Column(name = "DESCRIPTION", length = 200, nullable = false)
    var descriptionEng: String? = null,

    @Column(name = "OPIS_NIE", length = 200, nullable = true)
    var descriptionGer: String? = null,

    @Column(name = "OPIS_ROS", length = 200, nullable = true)
    var descriptionRus: String? = null,

    @Column(name = "OPIS_HIS", length = 200, nullable = true)
    var descriptionHis: String? = null,

    @Column(name = "OPIS_FRA", length = 200, nullable = true)
    var descriptionFra: String? = null,

    @Column(name = "KOD_POLON", length = 20, nullable = false)
    var polonCode: String,

    @OneToMany(mappedBy = "fieldOfStudy", fetch = FetchType.LAZY)
    var conductedFieldOfStudy: MutableList<ConductedFieldOfStudy>,

    @OneToMany(mappedBy = "fieldOfStudySpeciality", fetch = FetchType.LAZY)
    var conductedFieldOfStudySpeciality: MutableList<ConductedFieldOfStudy>
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FieldOfStudy

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}