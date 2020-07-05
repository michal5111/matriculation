package pl.poznan.ue.matriculation.local.domain.applications

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "ApplicationForeignerData")
class ApplicationForeignerData(

        @JsonIgnore
        @Id
        @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH, CascadeType.DETACH, CascadeType.REFRESH])
        @JoinColumn(name = "application_id", referencedColumnName = "id")
        var application: Application? = null,

        var baseOfStay: String?,

        var basisOfAdmission: String?,

        var sourceOfFinancing: String?
) : Serializable {

    override fun toString(): String {
        return "ApplicationForeignerData(baseOfStay=$baseOfStay, basisOfAdmission=$basisOfAdmission, " +
                "sourceOfFinancing=$sourceOfFinancing)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ApplicationForeignerData

        if (application != other.application) return false

        return true
    }

    override fun hashCode(): Int {
        return application?.hashCode() ?: 0
    }


}