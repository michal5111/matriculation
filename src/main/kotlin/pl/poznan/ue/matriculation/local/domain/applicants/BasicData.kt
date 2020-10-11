package pl.poznan.ue.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
class BasicData(

        @JsonIgnore
        @Id
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        var applicant: Applicant? = null,

        var sex: Char,

        var pesel: String?,

        @Temporal(TemporalType.DATE)
        var dateOfBirth: Date?,

        var cityOfBirth: String?,

        var countryOfBirth: String?,

        var dataSource: String
) : Serializable {

    override fun toString(): String {
        return "BasicData(sex='$sex', pesel=$pesel, dateOfBirth=$dateOfBirth, cityOfBirth='$cityOfBirth', " +
                "countryOfBirth='$countryOfBirth', dataSource='$dataSource')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BasicData

        if (applicant != other.applicant) return false

        return true
    }

    override fun hashCode(): Int {
        return applicant?.hashCode() ?: 0
    }


}