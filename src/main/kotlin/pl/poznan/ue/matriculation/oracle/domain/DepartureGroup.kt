package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.*

@Entity
@Table(name = "DZ_WYJ_GRUPY")
class DepartureGroup(

    @Id
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 200, nullable = false)
    val description: String,

    @Column(name = "UWAGI", nullable = true, length = 2000)
    var comments: String?,

    @OneToMany(mappedBy = "departureGroup", fetch = FetchType.LAZY)
    var arrivals: MutableList<Arrival>
) : BaseEntity()
