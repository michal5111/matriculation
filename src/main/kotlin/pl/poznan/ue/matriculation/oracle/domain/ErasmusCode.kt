package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.CacheConcurrencyStrategy
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "DZ_KODY_SOKRATES")
class ErasmusCode(

    @Id
    @Column(name = "KOD", length = 5, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 200, nullable = false)
    val description: String,

    @Column(name = "OPIS_ANG", length = 200, nullable = true)
    val descriptionEng: String?,

    @Column(name = "KOD_ISCED", nullable = true, length = 5)
    val iscedCode: String,

    @OneToMany(mappedBy = "erasmusCode", fetch = FetchType.LAZY)
    val arrivals: List<Arrival>
) : BaseEntity()
