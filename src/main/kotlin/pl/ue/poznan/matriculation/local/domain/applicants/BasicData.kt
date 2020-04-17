package pl.ue.poznan.matriculation.local.domain.applicants

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
data class BasicData(

        @JsonIgnore
        @Id
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicant_id", referencedColumnName = "id")
        var applicant: Applicant? = null,

        var sex: String,

        @Column(unique = true)
        var pesel: String?,

        var dateOfBirth: Date?,

        var cityOfBirth: String,

        var countryOfBirth: String,

        var dataSource: String
): Serializable {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as BasicData

                if (sex != other.sex) return false
                if (pesel != other.pesel) return false
                if (dateOfBirth != other.dateOfBirth) return false
                if (cityOfBirth != other.cityOfBirth) return false
                if (countryOfBirth != other.countryOfBirth) return false
                if (dataSource != other.dataSource) return false

                return true
        }

        override fun hashCode(): Int {
                var result = sex.hashCode()
                result = 31 * result + (pesel?.hashCode() ?: 0)
                result = 31 * result + (dateOfBirth?.hashCode() ?: 0)
                result = 31 * result + cityOfBirth.hashCode()
                result = 31 * result + countryOfBirth.hashCode()
                result = 31 * result + dataSource.hashCode()
                return result
        }

        override fun toString(): String {
                return "BasicData(sex='$sex', pesel=$pesel, dateOfBirth=$dateOfBirth, cityOfBirth='$cityOfBirth', countryOfBirth='$countryOfBirth', dataSource='$dataSource')"
        }


}