package pl.ue.poznan.matriculation.local.domain.applications

import pl.ue.poznan.matriculation.irk.domain.applications.ForeignerData
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class ForeignerData(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,
        val baseOfStay: String?,
        val basisOfAdmission: String?,
        val sourceOfFinancing: String?
) {
    constructor(foreignerData: ForeignerData) : this(
            baseOfStay = foreignerData.baseOfStay,
            basisOfAdmission = foreignerData.basisOfAdmission,
            sourceOfFinancing = foreignerData.sourceOfFinancing
    )
}