package pl.ue.poznan.matriculation.local.domain.applicants


import pl.ue.poznan.matriculation.irk.dto.applicants.AdditionalDataDTO
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class AdditionalData(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,
        val cityOfBirth: String?,
        val countryOfBirth: String?,
        val documentCountry: String?,
        val documentExpDate: String?,
        val documentNumber: String?,
        val documentType: String?,
        val fathersName: String?,
        val militaryCategory: String?,
        val militaryStatus: String?,
        val mothersName: String?,
        val wku: String?
) {
        constructor(additionalDataDTO: AdditionalDataDTO): this(
                cityOfBirth = additionalDataDTO.cityOfBirth,
                countryOfBirth = additionalDataDTO.countryOfBirth,
                documentCountry = additionalDataDTO.countryOfBirth,
                documentExpDate = additionalDataDTO.documentExpDate,
                documentNumber = additionalDataDTO.documentNumber,
                documentType = additionalDataDTO.documentType,
                fathersName = additionalDataDTO.fathersName,
                militaryCategory = additionalDataDTO.militaryCategory,
                militaryStatus = additionalDataDTO.militaryStatus,
                mothersName = additionalDataDTO.mothersName,
                wku = additionalDataDTO.wku
        )
}