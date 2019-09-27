package pl.ue.poznan.matriculation.local.domain.applicants


import pl.ue.poznan.matriculation.irk.domain.applicants.Document
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Document(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,
        val certificateType: String?,
        val certificateTypeCode: String?,
        val certificateUsosCode: String?,
        val comment: String?,
        val documentNumber: String?,
        val documentYear: Int?,
        val issueCity: String?,
        val issueCountry: String?,
        val issueDate: String?,
        val issueInstitution: String?,
        val issueInstitutionUsosCode: String?,
        val modificationDate: String?
) {
        constructor(document:Document): this(
                certificateType = document.certificateType,
                certificateTypeCode = document.certificateTypeCode,
                certificateUsosCode = document.certificateUsosCode,
                comment = document.comment,
                documentNumber = document.documentNumber,
                documentYear = document.documentYear,
                issueCity = document.issueCity,
                issueCountry = document.issueCountry,
                issueDate = document.issueDate,
                issueInstitution = document.issueInstitution,
                issueInstitutionUsosCode = document.issueInstitutionUsosCode,
                modificationDate = document.modificationDate
        )
}