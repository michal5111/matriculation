package pl.poznan.ue.matriculation.applicantDataSources

import org.springframework.stereotype.Component
import pl.poznan.ue.matriculation.irk.dto.Page
import pl.poznan.ue.matriculation.irk.dto.applicants.ApplicantDTO
import pl.poznan.ue.matriculation.irk.dto.applications.ApplicationDTO
import pl.poznan.ue.matriculation.irk.service.IrkService

@Component
class IrkApplicantDataSourceImpl(private val irkService: IrkService) : ApplicantDataSource {
    override fun getApplicationsPage(registration: String, programme: String, pageNumber: Int): Page<ApplicationDTO> {
        return irkService.getApplications(
                admitted = true,
                paid = true,
                registration = registration,
                programme = programme,
                pageNumber = pageNumber
        )
    }

    override fun getApplicantById(applicantId: Long): ApplicantDTO {
        return irkService.getApplicantById(applicantId)!!
    }

    override fun getPhoto(photoUrl: String): ByteArray {
        return irkService.getPhoto(photoUrl)
    }

    override fun getName(): String {
        return "IRK"
    }
}