package pl.poznan.ue.matriculation.oracle.domain

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "DZ_DOKUMENTY_UPR")
class EntitlementDocument(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_DOK_UPR_SEQ")
    @SequenceGenerator(sequenceName = "DZ_DOK_UPR_SEQ", allocationSize = 1, name = "DZ_DOK_UPR_SEQ")
    @Column(name = "ID", length = 10)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = false)
    var person: Person? = null,

    @Column(name = "RODZAJ", length = 1, nullable = false)
    var type: Char,

    @Column(name = "NUMER", length = 200, nullable = false)
    var number: String,

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_WYSTAWIENIA", nullable = false)
    var issueDate: Date,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SZK_ID", referencedColumnName = "ID", nullable = true)
    var school: School? = null,

    @Column(name = "OPIS", length = 500, nullable = true)
    var description: String? = null,

    @OneToMany(mappedBy = "entitlementDocument", fetch = FetchType.LAZY)
    val personProgrammes: MutableList<PersonProgramme> = mutableListOf(),
) : BaseEntity() {


    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    fun addPersonProgramme(personProgramme: PersonProgramme) {
        personProgrammes.add(personProgramme)
        personProgramme.entitlementDocument = this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EntitlementDocument) return false

        if (type != other.type) return false
        if (number != other.number) return false

        return true
    }

}
