package pl.poznan.ue.matriculation.local.domain.applicants

import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Status(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicantForeignerData_id", referencedColumnName = "applicant_id")
        var applicantForeignerData: ApplicantForeignerData? = null,

        val status: String
): Serializable {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Status

                if (id != other.id) return false
                if (status != other.status) return false

                return true
        }

        override fun hashCode(): Int {
                var result = id?.hashCode() ?: 0
                result = 31 * result + status.hashCode()
                return result
        }

        override fun toString(): String {
                return "Status(id=$id, status='$status')"
        }


}