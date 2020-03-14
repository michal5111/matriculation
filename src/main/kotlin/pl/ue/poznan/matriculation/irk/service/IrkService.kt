package pl.ue.poznan.matriculation.irk.service

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.util.UriComponentsBuilder
import pl.ue.poznan.matriculation.irk.domain.Page
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
    private val apiKey = "598050a3c1978e84cb7dc0f43d4e2a091ff76319"
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

    fun getApplications(
            admitted: Boolean,
            paid: Boolean,
            programme: String?,
            registration: String?,
            pageNumber: Int?): Page<ApplicationDTO>? {
        val uriComponentBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("${apiUrl}applications/")
        if (admitted) {
            uriComponentBuilder.queryParam("admitted",admitted)
        }
        if (paid) {
            uriComponentBuilder.queryParam("paid", paid)
        }
        registration?.let {
            uriComponentBuilder.queryParam("registration", registration)
        }
        programme?.let {
            uriComponentBuilder.queryParam("programme",programme)
        }
        if (pageNumber != null) {
            uriComponentBuilder.queryParam("page",pageNumber)
        }
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Page<ApplicationDTO>> = restTemplate.exchange(
                uriComponentBuilder.toUriString(),
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
