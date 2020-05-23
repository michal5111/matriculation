package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Name(

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
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Name

                if (middle != other.middle) return false
                if (family != other.family) return false
                if (given != other.given) return false
                if (maiden != other.maiden) return false

                return true
        }

        override fun hashCode(): Int {
                var result = middle?.hashCode() ?: 0
                result = 31 * result + (family?.hashCode() ?: 0)
                result = 31 * result + (given?.hashCode() ?: 0)
                result = 31 * result + (maiden?.hashCode() ?: 0)
                return result
        }

        override fun toString(): String {
                return "Name(middle=$middle, family=$family, given=$given, maiden=$maiden)"
        }


}