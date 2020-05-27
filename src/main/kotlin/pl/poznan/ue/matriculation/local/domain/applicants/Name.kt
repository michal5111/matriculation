package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class Name(

        @JsonIgnore
        @Id
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        var applicant: Applicant? = null,

        var middle: String?,
        var family: String?,
        var given: String?,
        var maiden: String?
): Serializable {

    override fun toString(): String {
        return "Name(middle=$middle, family=$family, given=$given, maiden=$maiden)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Name

        if (applicant != other.applicant) return false

        return true
        }

        override fun hashCode(): Int {
            return applicant?.hashCode() ?: 0
        }


}