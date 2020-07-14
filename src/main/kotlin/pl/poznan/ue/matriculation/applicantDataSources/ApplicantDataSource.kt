package pl.poznan.ue.matriculation.applicantDataSources

import pl.poznan.ue.matriculation.irk.dto.Page
import pl.poznan.ue.matriculation.irk.dto.applicants.ApplicantDTO
import pl.poznan.ue.matriculation.irk.dto.applications.ApplicationDTO

interface ApplicantDataSource {
    fun getApplicationsPage(registration: String, programme: String, pageNumber: Int): Page<ApplicationDTO>

    fun getApplicantById(applicantId: Long): ApplicantDTO

    fun getPhoto(photoUrl: String): ByteArray

    fun getName(): String
}