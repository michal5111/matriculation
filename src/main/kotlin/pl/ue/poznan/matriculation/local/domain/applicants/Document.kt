package pl.ue.poznan.matriculation.local.domain.applicants


import pl.ue.poznan.matriculation.irk.dto.applicants.DocumentDTO
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
        constructor(documentDTO:DocumentDTO): this(
                certificateType = documentDTO.certificateType,
                certificateTypeCode = documentDTO.certificateTypeCode,
                certificateUsosCode = documentDTO.certificateUsosCode,
                comment = documentDTO.comment,
                documentNumber = documentDTO.documentNumber,
                documentYear = documentDTO.documentYear,
                issueCity = documentDTO.issueCity,
                issueCountry = documentDTO.issueCountry,
                issueDate = documentDTO.issueDate,
                issueInstitution = documentDTO.issueInstitution,
                issueInstitutionUsosCode = documentDTO.issueInstitutionUsosCode,
                modificationDate = documentDTO.modificationDate
        )
}