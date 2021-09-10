package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_ATRYBUTY_WYMIANY")
class ExchangeAttribute(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_ATR_SEQ")
    @SequenceGenerator(sequenceName = "DZ_ATR_SEQ", allocationSize = 1, name = "DZ_ATR_SEQ")
    @Column(name = "ID", length = 10)
    val id: Long? = null,

    @Column(name = "NAZWA", nullable = false, length = 20)
    val name: String,

    @Column(name = "WARTOSC", nullable = false, length = 100)
    val value: String,

    @Column(name = "UWAGI", nullable = true, length = 100)
    val comments: String?,

    @OneToMany(mappedBy = "exchangeAttribute", fetch = FetchType.LAZY)
    val contracts: MutableList<Contract> = mutableListOf(),

    @OneToMany(mappedBy = "arrivingStudiesLevel", fetch = FetchType.LAZY)
    val arrivingCooperations: MutableList<Cooperation>,

    @OneToMany(mappedBy = "leavingStudiesLevel", fetch = FetchType.LAZY)
    val leavingCooperations: MutableList<Cooperation>,

    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
    val typeCooperations: MutableList<Cooperation>,

    @OneToMany(mappedBy = "extensionReasonExchangeAttribute", fetch = FetchType.LAZY)
    val arrivalsExtensionReasons: MutableList<Arrival>,

    @OneToMany(mappedBy = "typeOfStudiesExchangeAttribute", fetch = FetchType.LAZY)
    val arrivalsTypeOfStudies: MutableList<Arrival>,

    @OneToMany(mappedBy = "formOfEducationExchangeAttribute", fetch = FetchType.LAZY)
    val arrivalsFormOfEducations: MutableList<Arrival>
) : BaseEntity()
