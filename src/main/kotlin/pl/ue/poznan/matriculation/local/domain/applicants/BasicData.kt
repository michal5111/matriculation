package pl.ue.poznan.matriculation.local.domain.applicants

import pl.ue.poznan.matriculation.irk.domain.applicants.BasicData
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class BasicData(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,

        val sex: String?,

        val pesel: String?,

        val dateOfBirth: Date?,

        val cityOfBirth: String?,

        val countryOfBirth: String?,

        val dataSource: String
) {
        constructor(basicData: BasicData): this(
                sex = basicData.sex,
                pesel = basicData.pesel,
                dateOfBirth = basicData.dateOfBirth,
                countryOfBirth = basicData.countryOfBirth,
                cityOfBirth = basicData.cityOfBirth,
                dataSource = basicData.dataSource
        )
}