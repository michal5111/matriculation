package pl.poznan.ue.matriculation.oracle.domain

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "DZ_UMOWY")
class Contract(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_ADR_SEQ")
        @SequenceGenerator(sequenceName = "DZ_ADR_SEQ", allocationSize = 1, name = "DZ_ADR_SEQ")
        @Column(name = "ID", length = 10)
        val id: Long? = null,

        @Column(name = "RODZAJ", nullable = false)
        val type: Int,

        @Column(name = "DATA_POCZ", nullable = false)
        val startDate: Date,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "STATUS", referencedColumnName = "ID", nullable = false)
        val exchangeAttribute: ExchangeAttribute,

        @Column(name = "DATA_KON", nullable = true)
        val endDate: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "SZK_ID", referencedColumnName = "ID", nullable = true)
        val school: School?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "KRAJ", referencedColumnName = "KOD", nullable = true)
        val country: Citizenship?,

        @Column(name = "UWAGI", nullable = true, length = 4000)
        val comments: String?,

        @Column(name = "NUMER", nullable = false, length = 100)
        val number: String,

        @Column(name = "DATA_PODPIS", nullable = false)
        val signatureDate: Date,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = true)
        var person: Person? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PRAC_ID_KI", referencedColumnName = "ID", nullable = true)
        val coordinatorEmployee: Employee?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OSZ_ID_KI")
        val coordinatorExternalPerson: ExternalPerson?,

        @Column(name = "CZY_MIGROWAC", nullable = false, length = 1)
        val migrate: Char,

        @Column(name = "ID_EWP", nullable = true, length = 64)
        val ewpID: String?,

        @Column(name = "GUID", nullable = false, length = 32)
        val guid: String,

        @Column(name = "HASH_PARTNERA_EWP", nullable = true, length = 64)
        val ewpPartnerHash: String?,

        @OneToMany(mappedBy = "contract")
        val arrivals: MutableList<Arrival>,

        @OneToMany(mappedBy = "contract")
        val cooperations: MutableList<Cooperation>
)