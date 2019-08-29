package pl.ue.poznan.matriculation.irk.service

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import pl.ue.poznan.matriculation.irk.domain.Page
import pl.ue.poznan.matriculation.irk.domain.applicants.Applicant
import pl.ue.poznan.matriculation.irk.domain.applications.Application
import pl.ue.poznan.matriculation.irk.domain.programmes.ProgrammeGroups
import pl.ue.poznan.matriculation.irk.domain.registrations.Registration


@Service
class IrkService {
    internal val apiKey = "50b962525772029436cf6643f0e2f569e75f967f"
    internal val apiUrl = "https://usos-irk.ue.poznan.pl/api/"
    private val restTemplate: RestTemplate = RestTemplate()
    private class pageOfApplicants: ParameterizedTypeReference<Page<Applicant>>()

    fun getApplicantById(id: Long): Applicant? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Applicant> = restTemplate.exchange(
                "${apiUrl}applicants/$id",
                HttpMethod.GET,
                httpEntity,
                Applicant::javaClass
        )
        return response.body
    }

    fun getApplicantsByPesel(pesel: String): Page<Applicant>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Page<Applicant>> = restTemplate.exchange(
                "${apiUrl}applicants/?pesel=$pesel",
                HttpMethod.GET,
                httpEntity,
                pageOfApplicants()
        )
        return response.body
    }

    fun getApplicantsBySurname(surname: String): Page<Applicant>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Page<Applicant>> = restTemplate.exchange(
                "${apiUrl}applicants/?surname=$surname",
                HttpMethod.GET,
                httpEntity,
                pageOfApplicants()
        )
        return response.body
    }

    fun getApplicantsByEmail(email: String): Page<Applicant>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Page<Applicant>> = restTemplate.exchange(
                "${apiUrl}applicants/?email=$email",
                HttpMethod.GET,
                httpEntity,
                pageOfApplicants()
        )
        return response.body
    }

    fun getRegistration(id: String): Registration? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Registration> = restTemplate.exchange(
                "${apiUrl}registrations/$id",
                HttpMethod.GET,
                httpEntity,
                Registration::javaClass
        )
        return response.body
    }

    fun getApplication(id: Long): Application? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Application> = restTemplate.exchange(
                "${apiUrl}applications/$id",
                HttpMethod.GET,
                httpEntity,
                Application::javaClass
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

    fun getProgrammesGroups(id: String): ProgrammeGroups? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization","Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<ProgrammeGroups> = restTemplate.exchange(
                "${apiUrl}programme-groups/$id",
                HttpMethod.GET,
                httpEntity,
                ProgrammeGroups::javaClass
        )
        return response.body
    }
}