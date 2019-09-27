package pl.ue.poznan.matriculation.local.domain.applicants


import pl.ue.poznan.matriculation.irk.domain.applicants.AdditionalData
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
        constructor(additionalData: AdditionalData): this(
                cityOfBirth = additionalData.cityOfBirth,
                countryOfBirth = additionalData.countryOfBirth,
                documentCountry = additionalData.countryOfBirth,
                documentExpDate = additionalData.documentExpDate,
                documentNumber = additionalData.documentNumber,
                documentType = additionalData.documentType,
                fathersName = additionalData.fathersName,
                militaryCategory = additionalData.militaryCategory,
                militaryStatus = additionalData.militaryStatus,
                mothersName = additionalData.mothersName,
                wku = additionalData.wku
        )
}