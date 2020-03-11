package pl.ue.poznan.matriculation.local.domain.applicants

import pl.ue.poznan.matriculation.irk.dto.applicants.BasicDataDTO
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
        constructor(basicDataDTO: BasicDataDTO): this(
                sex = basicDataDTO.sex,
                pesel = basicDataDTO.pesel,
                dateOfBirth = basicDataDTO.dateOfBirth,
                countryOfBirth = basicDataDTO.countryOfBirth,
                cityOfBirth = basicDataDTO.cityOfBirth,
                dataSource = basicDataDTO.dataSource
        )
}