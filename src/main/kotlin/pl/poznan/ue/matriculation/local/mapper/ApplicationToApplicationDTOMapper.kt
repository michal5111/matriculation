package pl.poznan.ue.matriculation.local.mapper

import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.dto.ApplicantBasicDto
import pl.poznan.ue.matriculation.local.dto.ApplicationBasicDto
import pl.poznan.ue.matriculation.local.dto.DocumentDto

class ApplicationToApplicationDTOMapper {
    fun map(application: Application): ApplicationBasicDto {
        return ApplicationBasicDto(
            id = application.id,
            foreignId = application.foreignId,
            dataSourceId = application.dataSourceId,
            editUrl = application.editUrl,
            importStatus = application.importStatus,
            importError = application.importError,
            stackTrace = application.stackTrace,
            certificate = application.certificate?.let {
                DocumentDto(
                    applicantId = application.id,
                    certificateType = it.certificateType,
                    certificateTypeCode = it.certificateTypeCode,
                    certificateUsosCode = it.certificateUsosCode,
                    comment = it.comment,
                    documentNumber = it.documentNumber,
                    documentYear = it.documentYear,
                    issueCity = it.issueCity,
                    issueCountry = it.issueCountry,
                    issueDate = it.issueDate,
                    issueInstitution = it.issueInstitution,
                    issueInstitutionUsosCode = it.issueInstitutionUsosCode,
                    modificationDate = it.modificationDate
                )
            },
            applicant = application.applicant?.let {
                ApplicantBasicDto(
                    foreignId = it.foreignId,
                    dataSourceId = it.dataSourceId,
                    email = it.email,
                    indexNumber = it.indexNumber,
                    citizenship = it.citizenship,
                    nationality = it.nationality,
                    photo = it.photo,
                    photoPermission = it.photoPermission,
                    modificationDate = it.modificationDate,
                    usosId = it.usosId
                )
            },
            importId = application.import?.id
        )
    }
}
