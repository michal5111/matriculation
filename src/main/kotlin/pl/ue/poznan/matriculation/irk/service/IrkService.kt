package pl.ue.poznan.matriculation.irk.service

import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.util.UriComponentsBuilder
import pl.ue.poznan.matriculation.irk.dto.Page
import pl.ue.poznan.matriculation.irk.dto.applicants.ApplicantDTO
import pl.ue.poznan.matriculation.irk.dto.applications.ApplicationDTO
import pl.ue.poznan.matriculation.irk.dto.programmes.ProgrammeGroupsDTO
import pl.ue.poznan.matriculation.irk.dto.registrations.RegistrationDTO
import pl.ue.poznan.matriculation.local.domain.applicants.Applicant
import pl.ue.poznan.matriculation.local.domain.applications.Application
import pl.ue.poznan.matriculation.local.domain.programmes.ProgrammeGroups
import pl.ue.poznan.matriculation.local.domain.registrations.Registration
import java.io.InputStream
import javax.annotation.PostConstruct


@Service
class IrkService {

    @Value("\${pl.ue.poznan.matriculation.irkInstance}")
    private lateinit var serviceUrl: String

    @Value("\${pl.ue.poznan.matriculation.irkInstanceKey}")
    private lateinit var apiKey: String

    private lateinit var apiUrl: String

    private val restTemplate: RestTemplate = RestTemplate()

    private class PageOfApplicants : ParameterizedTypeReference<Page<ApplicantDTO>>()
    private class PageOfApplications : ParameterizedTypeReference<Page<ApplicationDTO>>()
    private class PageOfRegistrations : ParameterizedTypeReference<Page<RegistrationDTO>>()

    @PostConstruct
    fun init() {
        apiUrl = "$serviceUrl/api/"
    }

    fun getApplicantById(id: Long): ApplicantDTO? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
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
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Page<ApplicantDTO>> = restTemplate.exchange(
                "${apiUrl}applicants/?pesel=$pesel",
                HttpMethod.GET,
                httpEntity,
                PageOfApplicants()
        )
        return response.body
    }

    fun getApplicantsBySurname(surname: String): Page<ApplicantDTO>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Page<ApplicantDTO>> = restTemplate.exchange(
                "${apiUrl}applicants/?surname=$surname",
                HttpMethod.GET,
                httpEntity,
                PageOfApplicants()
        )
        return response.body
    }

    fun getApplicantsByEmail(email: String): Page<ApplicantDTO>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Page<ApplicantDTO>> = restTemplate.exchange(
                "${apiUrl}applicants/?email=$email",
                HttpMethod.GET,
                httpEntity,
                PageOfApplicants()
        )
        return response.body
    }

    fun getRegistration(id: String): RegistrationDTO? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<RegistrationDTO> = restTemplate.exchange(
                "${apiUrl}registrations/$id",
                HttpMethod.GET,
                httpEntity,
                Registration::javaClass
        )
        return response.body
    }

    fun getAvailableRegistrations(): MutableList<Map<String, String>> {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val availableRegistrations = mutableListOf<Map<String, String>>()
        var currentPage = 1
        var hasNext: Boolean
        do {
            val page = restTemplate.exchange(
                    "${apiUrl}registrations/?page=$currentPage&status=published",
                    HttpMethod.GET,
                    httpEntity,
                    PageOfRegistrations()
            ).body
            page?.results?.forEach {
                val registration: Map<String, String> = mutableMapOf(
                        "code" to it.code,
                        "name" to it.name.pl!!
                )
                availableRegistrations.add(registration)
            }
            hasNext = page?.next != null
            currentPage++
        } while (hasNext)
        return availableRegistrations
    }

    fun getAvailableRegistrationProgrammes(registrationCode: String): List<String?> {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val availableProgrammes = mutableListOf<String>()
        val response: ResponseEntity<RegistrationDTO> = restTemplate.exchange(
                "${apiUrl}registrations/$registrationCode",
                HttpMethod.GET,
                httpEntity,
                RegistrationDTO::javaClass
        )
        return response.body!!.programmes
    }

    fun getApplication(id: Long): ApplicationDTO? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
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
            pageNumber: Int?): Page<ApplicationDTO> {
        val uriComponentBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("${apiUrl}applications/")
        if (admitted) {
            uriComponentBuilder.queryParam("admitted", admitted)
        }
        if (paid) {
            uriComponentBuilder.queryParam("paid", paid)
        }
        registration?.let {
            uriComponentBuilder.queryParam("registration", registration)
        }
        programme?.let {
            uriComponentBuilder.queryParam("programme", programme)
        }
        if (pageNumber != null) {
            uriComponentBuilder.queryParam("page", pageNumber)
        }
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        println(uriComponentBuilder.toUriString())
        val response: ResponseEntity<Page<ApplicationDTO>> = restTemplate.exchange(
                uriComponentBuilder.toUriString(),
                HttpMethod.GET,
                httpEntity,
                PageOfApplications()
        )
        response.body?.results?.forEach {
            it.irkInstance = serviceUrl
        }
        return response.body!!
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
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<ProgrammeGroupsDTO> = restTemplate.exchange(
                "${apiUrl}programme-groups/$id",
                HttpMethod.GET,
                httpEntity,
                ProgrammeGroups::javaClass
        )
        return response.body
    }

    fun getPhoto(photoUrl: String): ByteArray {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val responseEntity: ResponseEntity<Resource> = restTemplate.exchange(
                "$serviceUrl$photoUrl",
                HttpMethod.GET,
                httpEntity,
                Resource::class.java
        )
        val responseInputStream: InputStream
        try {
            responseInputStream = responseEntity.body!!.inputStream
        } catch (e: Exception) {
            throw IllegalStateException()
        }
        return IOUtils.toByteArray(responseInputStream)
    }
}
