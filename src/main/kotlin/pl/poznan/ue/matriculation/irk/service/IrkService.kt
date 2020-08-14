package pl.poznan.ue.matriculation.irk.service

import org.apache.commons.io.IOUtils
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.util.UriComponentsBuilder
import pl.poznan.ue.matriculation.irk.dto.Page
import pl.poznan.ue.matriculation.irk.dto.applicants.DocumentDTO
import pl.poznan.ue.matriculation.irk.dto.applicants.IrkApplicantDto
import pl.poznan.ue.matriculation.irk.dto.applicants.MatriculationDataDTO
import pl.poznan.ue.matriculation.irk.dto.applications.IrkApplicationDTO
import pl.poznan.ue.matriculation.irk.dto.programmes.ProgrammeGroupsDTO
import pl.poznan.ue.matriculation.irk.dto.registrations.RegistrationDTO
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.programmes.ProgrammeGroups
import pl.poznan.ue.matriculation.local.domain.registrations.Registration


class IrkService(
        val serviceUrl: String,
        private val apiKey: String
) {

    private var apiUrl: String = "$serviceUrl/api/"

    private val restTemplate: RestTemplate = RestTemplate()

    private class PageOfApplicants : ParameterizedTypeReference<Page<IrkApplicantDto>>()
    private class PageOfApplications : ParameterizedTypeReference<Page<IrkApplicationDTO>>()
    private class PageOfRegistrations : ParameterizedTypeReference<Page<RegistrationDTO>>()
    private class MapResult : ParameterizedTypeReference<Map<String, String>>()

    fun getApplicantById(id: Long): IrkApplicantDto? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<IrkApplicantDto> = restTemplate.exchange(
                "${apiUrl}applicants/$id",
                HttpMethod.GET,
                httpEntity,
                Applicant::javaClass
        )
        return response.body
    }

    fun getApplicantsByPesel(pesel: String): Page<IrkApplicantDto>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Page<IrkApplicantDto>> = restTemplate.exchange(
                "${apiUrl}applicants/?pesel=$pesel",
                HttpMethod.GET,
                httpEntity,
                PageOfApplicants()
        )
        return response.body
    }

    fun getApplicantsBySurname(surname: String): Page<IrkApplicantDto>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Page<IrkApplicantDto>> = restTemplate.exchange(
                "${apiUrl}applicants/?surname=$surname",
                HttpMethod.GET,
                httpEntity,
                PageOfApplicants()
        )
        return response.body
    }

    fun getApplicantsByEmail(email: String): Page<IrkApplicantDto>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Page<IrkApplicantDto>> = restTemplate.exchange(
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

    fun getAvailableRegistrationsPage(pageNumber: Int): Page<RegistrationDTO>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        return restTemplate.exchange(
                "${apiUrl}registrations/?page=$pageNumber",
                HttpMethod.GET,
                httpEntity,
                PageOfRegistrations()
        ).body
    }

    fun getAvailableRegistrationProgrammes(registrationCode: String): List<String> {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<RegistrationDTO> = restTemplate.exchange(
                "${apiUrl}registrations/$registrationCode",
                HttpMethod.GET,
                httpEntity,
                RegistrationDTO::javaClass
        )
        return response.body!!.programmes
    }

    fun getApplication(id: Long): IrkApplicationDTO? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<IrkApplicationDTO> = restTemplate.exchange(
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
            pageNumber: Int?): Page<IrkApplicationDTO> {
        val uriComponentBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("${apiUrl}applications/")
        if (admitted) {
            uriComponentBuilder.queryParam("admitted", admitted)
        }
        if (paid) {
            uriComponentBuilder.queryParam("paid", paid)
        }
        registration?.let {
            uriComponentBuilder.queryParam("registration", "^${registration}$")
        }
        programme?.let {
            uriComponentBuilder.queryParam("programme", "^${programme}$")
        }
        if (pageNumber != null) {
            uriComponentBuilder.queryParam("page", pageNumber)
        }
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<Page<IrkApplicationDTO>> = restTemplate.exchange(
                uriComponentBuilder.build().toUri(),
                HttpMethod.GET,
                httpEntity,
                PageOfApplications()
        )
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
        var byteArray = ByteArray(0)
        responseEntity.body!!.inputStream.use {
            byteArray = IOUtils.toByteArray(it)
        }
        return byteArray
    }

    fun completeImmatriculation(applicationId: Long): ResponseEntity<Map<String, String>> {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        return restTemplate.exchange(
                "${apiUrl}matriculation/${applicationId}/complete/",
                HttpMethod.POST,
                httpEntity,
                MapResult::class
        )
    }

    fun getMatriculationData(applicationId: Long): MatriculationDataDTO? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "Token $apiKey")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<MatriculationDataDTO> = restTemplate.exchange(
                "${apiUrl}matriculation/${applicationId}/data/",
                HttpMethod.GET,
                httpEntity,
                MatriculationDataDTO::class
        )
        return response.body
    }

    fun getPrimaryCertificate(applicationId: Long): DocumentDTO? {
        return getMatriculationData(applicationId)?.application?.certificate
    }
}
