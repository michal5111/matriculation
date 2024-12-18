package pl.poznan.ue.matriculation.irk.service

import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.util.UriComponentsBuilder
import pl.poznan.ue.matriculation.irk.dto.NotificationDto
import pl.poznan.ue.matriculation.irk.dto.Page
import pl.poznan.ue.matriculation.irk.dto.UserDto
import pl.poznan.ue.matriculation.irk.dto.applicants.IrkApplicantDto
import pl.poznan.ue.matriculation.irk.dto.applicants.MatriculationDataDTO
import pl.poznan.ue.matriculation.irk.dto.applications.IrkApplicationDTO
import pl.poznan.ue.matriculation.irk.dto.programmes.ProgrammeGroupsDTO
import pl.poznan.ue.matriculation.irk.dto.registrations.RegistrationDTO
import pl.poznan.ue.matriculation.irk.dto.registrations.RegistrationStatus
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.programmes.ProgrammeGroups
import pl.poznan.ue.matriculation.local.domain.registrations.Registration


class IrkService(
    val serviceUrl: String,
    apiKey: String
) {

    private var apiUrl: String = "$serviceUrl/api/"

    private val restTemplate: RestTemplate = RestTemplate()

    private val httpHeaders = HttpHeaders()

    private val httpEntity: HttpEntity<Any>

    init {
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders["Authorization"] = "Token $apiKey"
        httpEntity = HttpEntity(httpHeaders)
    }

    fun getApplicantById(id: Long): IrkApplicantDto? {
        val response: ResponseEntity<UserDto> = restTemplate.exchange(
            "${apiUrl}user/data/?id=$id",
            HttpMethod.GET,
            httpEntity,
            UserDto::class.java
        )
        return response.body?.user
    }

    fun getApplicantsByPesel(pesel: String): Page<IrkApplicantDto>? {
        val response: ResponseEntity<Page<IrkApplicantDto>> = restTemplate.exchange(
            "${apiUrl}applicants/?pesel=$pesel",
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<Page<IrkApplicantDto>>() {}
        )
        return response.body
    }

    fun getApplicantsBySurname(surname: String): Page<IrkApplicantDto>? {
        val response: ResponseEntity<Page<IrkApplicantDto>> = restTemplate.exchange(
            "${apiUrl}applicants/?surname=$surname",
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<Page<IrkApplicantDto>>() {}
        )
        return response.body
    }

    fun getApplicantsByEmail(email: String): Page<IrkApplicantDto>? {
        val response: ResponseEntity<Page<IrkApplicantDto>> = restTemplate.exchange(
            "${apiUrl}applicants/?email=$email",
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<Page<IrkApplicantDto>>() {}
        )
        return response.body
    }

    fun getRegistration(id: String): RegistrationDTO? {
        val response: ResponseEntity<RegistrationDTO> = restTemplate.exchange(
            "${apiUrl}registrations/$id",
            HttpMethod.GET,
            httpEntity,
            Registration::javaClass
        )
        return response.body
    }

    fun getAvailableRegistrationsPage(
        pageNumber: Int,
        code: Regex? = null,
        tag: String? = null,
        status: RegistrationStatus?,
        programme: Regex? = null
    ): Page<RegistrationDTO>? {
        val builder = UriComponentsBuilder.fromHttpUrl("${apiUrl}registrations/")
            .queryParam("page", pageNumber)
        if (code != null) {
            builder.queryParam("code", code.pattern)
        }
        if (tag != null) {
            builder.queryParam("tag", tag)
        }
        if (status != null) {
            builder.queryParam("status", status.status)
        }
        if (programme != null) {
            builder.queryParam("programme", programme.pattern)
        }
        val url = builder.build().toUriString()
        return restTemplate.exchange(
            url,
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<Page<RegistrationDTO>>() {}
        ).body
    }

    fun getAvailableRegistrationProgrammes(registrationCode: String): List<String> {
        val response: ResponseEntity<RegistrationDTO> = restTemplate.exchange(
            "${apiUrl}registrations/$registrationCode",
            HttpMethod.GET,
            httpEntity,
            RegistrationDTO::javaClass
        )
        return response.body!!.programmes
    }

    fun getApplication(id: Long): IrkApplicationDTO? {
        val response: ResponseEntity<IrkApplicationDTO> = restTemplate.exchange(
            "${apiUrl}applications/$id",
            HttpMethod.GET,
            httpEntity,
            Application::javaClass
        )
        return response.body
    }

    fun getApplications(
        admitted: Boolean = true,
        paid: Boolean = true,
        programme: String?,
        qualified: Boolean = true,
        registration: String?,
        pageNumber: Int?,
        pageLength: Int? = 20
    ): Page<IrkApplicationDTO> {
        val uriComponentBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("${apiUrl}applications/")
        if (admitted) {
            uriComponentBuilder.queryParam("admitted", admitted)
        }
        if (paid) {
            uriComponentBuilder.queryParam("paid", paid)
        }
        if (qualified) {
            uriComponentBuilder.queryParam("qualified", qualified)
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
        pageNumber?.let {
            uriComponentBuilder.queryParam("page_length", pageLength)
        }
        val response: ResponseEntity<Page<IrkApplicationDTO>> = restTemplate.exchange(
            uriComponentBuilder.build().toUri(),
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<Page<IrkApplicationDTO>>() {}
        )
        return response.body!!
    }

    fun getProgrammesGroups(id: String): ProgrammeGroupsDTO? {
        val response: ResponseEntity<ProgrammeGroupsDTO> = restTemplate.exchange(
            "${apiUrl}programme-groups/$id",
            HttpMethod.GET,
            httpEntity,
            ProgrammeGroups::javaClass
        )
        return response.body
    }

    fun getPhoto(photoUrl: String): ByteArray? {
        val responseEntity: ResponseEntity<Resource> = restTemplate.exchange(
            "$serviceUrl$photoUrl",
            HttpMethod.GET,
            httpEntity,
            Resource::class.java
        )
        var byteArray: ByteArray?
        responseEntity.body?.inputStream.use {
            byteArray = it?.readBytes()
        }
        return byteArray
    }

    fun completeImmatriculation(applicationId: Long): ResponseEntity<Map<String, String>> {
        val request: HttpEntity<Map<String, String>> =
            HttpEntity(mapOf("username" to "Immatrykulator5000"), httpHeaders)
        return restTemplate.exchange(
            "${apiUrl}matriculation/${applicationId}/complete/",
            HttpMethod.POST,
            request,
            object : ParameterizedTypeReference<Map<String, String>>() {}
        )
    }

    fun getMatriculationData(applicationId: Long): MatriculationDataDTO? {
        val response: ResponseEntity<MatriculationDataDTO> = restTemplate.exchange(
            "${apiUrl}matriculation/${applicationId}/data/",
            HttpMethod.GET,
            httpEntity,
            MatriculationDataDTO::class
        )
        return response.body
    }

    fun sendNotification(userId: Long, notificationDto: NotificationDto): ResponseEntity<Map<String, String>> {
        val request: HttpEntity<Any> = HttpEntity(notificationDto, httpHeaders)
        return restTemplate.exchange(
            "${apiUrl}uep/user/${userId}/notify/",
            HttpMethod.POST,
            request,
            object : ParameterizedTypeReference<Map<String, String>>() {}
        )
    }
}
