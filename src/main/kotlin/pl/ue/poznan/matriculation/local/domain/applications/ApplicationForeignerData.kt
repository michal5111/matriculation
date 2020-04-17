package pl.ue.poznan.matriculation.local.domain.applications

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "ApplicationForeignerData")
data class ApplicationForeignerData(

        @JsonIgnore
        @Id
        @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH, CascadeType.DETACH, CascadeType.REFRESH])
        @JoinColumn(name = "application_id", referencedColumnName = "id")
        var application: Application? = null,

        var baseOfStay: String?,

        var basisOfAdmission: String?,

        var sourceOfFinancing: String?
): Serializable {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as ApplicationForeignerData

                if (baseOfStay != other.baseOfStay) return false
                if (basisOfAdmission != other.basisOfAdmission) return false
                if (sourceOfFinancing != other.sourceOfFinancing) return false

                return true
        }

        override fun hashCode(): Int {
                var result = baseOfStay?.hashCode() ?: 0
                result = 31 * result + (basisOfAdmission?.hashCode() ?: 0)
                result = 31 * result + (sourceOfFinancing?.hashCode() ?: 0)
                return result
        }

        override fun toString(): String {
                return "ApplicationForeignerData(baseOfStay=$baseOfStay, basisOfAdmission=$basisOfAdmission, sourceOfFinancing=$sourceOfFinancing)"
        }


}