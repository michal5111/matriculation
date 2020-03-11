package pl.ue.poznan.matriculation.irk.service

import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.domain.Page
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import pl.ue.poznan.matriculation.irk.dto.applicants.ApplicantDTO
import pl.ue.poznan.matriculation.irk.dto.applications.ApplicationDTO
import pl.ue.poznan.matriculation.irk.dto.programmes.ProgrammeGroupsDTO
import pl.ue.poznan.matriculation.irk.dto.registrations.RegistrationDTO
import pl.ue.poznan.matriculation.local.domain.applicants.Applicant
import pl.ue.poznan.matriculation.local.domain.applications.Application
import pl.ue.poznan.matriculation.local.domain.programmes.ProgrammeGroups
import pl.ue.poznan.matriculation.local.domain.registrations.Registration


@Service
class IrkService {
    private val apiKey = "50b962525772029436cf6643f0e2f569e75f967f"
    private val apiUrl = "https://usos-irk.ue.poznan.pl/api/"
    private val restTemplate: RestTemplate = RestTemplate()
    private class pageOfApplicants: ParameterizedTypeReference<Page<ApplicantDTO>>()
    private class pageOfApplications: ParameterizedTypeReference<Page<ApplicationDTO>>()

    fun getApplicantById(id: Long): ApplicantDTO? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<ApplicantDTO> = restTemplate.exchange(
                "${apiUrl}applicants/$id",
                HttpMethod.GET,
                httpEntity,
                Applicant::javaClass
        )
        return response.body
    }

    fun getApplicantsByPesel(pesel: String): Page<ApplicantDTO>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Page<ApplicantDTO>> = restTemplate.exchange(
                "${apiUrl}applicants/?pesel=$pesel",
                HttpMethod.GET,
                httpEntity,
                pageOfApplicants()
        )
        return response.body
    }

    fun getApplicantsBySurname(surname: String): Page<ApplicantDTO>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Page<ApplicantDTO>> = restTemplate.exchange(
                "${apiUrl}applicants/?surname=$surname",
                HttpMethod.GET,
                httpEntity,
                pageOfApplicants()
        )
        return response.body
    }

    fun getApplicantsByEmail(email: String): Page<ApplicantDTO>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Page<ApplicantDTO>> = restTemplate.exchange(
                "${apiUrl}applicants/?email=$email",
                HttpMethod.GET,
                httpEntity,
                pageOfApplicants()
        )
        return response.body
    }

    fun getRegistration(id: String): RegistrationDTO? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<RegistrationDTO> = restTemplate.exchange(
                "${apiUrl}registrations/$id",
                HttpMethod.GET,
                httpEntity,
                Registration::javaClass
        )
        return response.body
    }

    fun getApplication(id: Long): ApplicationDTO? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<ApplicationDTO> = restTemplate.exchange(
                "${apiUrl}applications/$id",
                HttpMethod.GET,
                httpEntity,
                Application::javaClass
        )
        return response.body
    }

    fun getApplications(qualified: Boolean, paid: Boolean, pageNumber: Int?): Page<ApplicationDTO>? {
        var url = "${apiUrl}applications/?"
        if (qualified) {
            url = url.plus("qualified&")
        }
        if (paid) {
            url = url.plus("paid&")
        }
        if (pageNumber != null) {
            url = url.plus("page=$pageNumber&")
        }
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Page<ApplicationDTO>> = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntity,
                pageOfApplications()
        )
        return response.body
    }

//    fun getProgramme(id: Long): Application? {
//        val httpHeaders: HttpHeaders = HttpHeaders()
//        httpHeaders.contentType = MediaType.APPLICATION_JSON
//        httpHeaders.set("Authorization","Token $apiKey")
//        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
//        val response: ResponseEntity<Programme> = restTemplate.exchange(
//                "${apiUrl}applications/$id",
//                HttpMethod.GET,
//                httpEntity,
//                Application::javaClass
//        )
//        return response.body
//    }

    fun getProgrammesGroups(id: String): ProgrammeGroupsDTO? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<ProgrammeGroupsDTO> = restTemplate.exchange(
                "${apiUrl}programme-groups/$id",
                HttpMethod.GET,
                httpEntity,
                ProgrammeGroups::javaClass
        )
        return response.body
    }
}