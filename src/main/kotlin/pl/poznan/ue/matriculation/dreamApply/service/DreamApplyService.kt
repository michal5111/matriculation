package pl.poznan.ue.matriculation.dreamApply.service

import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import pl.poznan.ue.matriculation.dreamApply.dto.academicTerms.AcademicTermDto
import pl.poznan.ue.matriculation.dreamApply.dto.academicTerms.CourseDto
import pl.poznan.ue.matriculation.dreamApply.dto.applicant.ApplicationCourseDto
import pl.poznan.ue.matriculation.dreamApply.dto.applicant.DreamApplyApplicantDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.*
import pl.poznan.ue.matriculation.dreamApply.dto.email.EmailDto

class DreamApplyService(
    val instanceUrl: String,
    apiKey: String
) {
    private val restTemplate: RestTemplate = RestTemplate()

    private val apiUrl = "$instanceUrl/api/"

    private val httpHeaders = HttpHeaders()

    private val httpEntity: HttpEntity<Any>

    init {
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "DREAM apikey=\"$apiKey\"")
        httpEntity = HttpEntity(httpHeaders)
    }

    fun getApplicationsCountByFilter(
        academicTermID: String? = null,
        academicYear: String? = null,
        additionalFilters: Map<String, String>?
    ): Long {
        val uriComponentBuilder: UriComponentsBuilder = getUriComponentBuilder()
        academicTermID?.run {
            uriComponentBuilder.queryParam("byAcademicTermID", academicTermID)
        }
        academicYear?.run {
            uriComponentBuilder.queryParam("byAcademicYear", academicYear)
        }
        additionalFilters?.forEach { (filterName, filterValue) ->
            uriComponentBuilder.queryParam(filterName, filterValue)
        }
        val response: ResponseEntity<Any> = restTemplate.exchange(
            uriComponentBuilder.build().toUri(),
            HttpMethod.HEAD,
            httpEntity,
            Any::class.java
        )
        return response.headers.getFirst("X-Count")!!.toLong()
    }

    fun getApplicationsByFilter(
        academicTermID: String? = null,
        academicYear: String? = null,
        additionalFilters: Map<String, String>? = null,
        limit: Int? = null,
        expandApplicant: Boolean = false,
        expandOffer: Boolean = false
    ): Map<Long, DreamApplyApplicationDto>? {
        if (academicTermID == null && academicYear == null) {
            throw IllegalArgumentException("AcademicTerm and academicYear are null")
        }
        val uriComponentBuilder: UriComponentsBuilder = getUriComponentBuilder()
        academicTermID?.run {
            uriComponentBuilder.queryParam("byAcademicTermID", academicTermID)
        }
        academicYear?.run {
            uriComponentBuilder.queryParam("byAcademicYear", academicYear)
        }
        additionalFilters?.forEach { (filterName, filterValue) ->
            uriComponentBuilder.queryParam(filterName, filterValue)
        }
        limit?.let {
            uriComponentBuilder.queryParam("limit", limit)
        }
        val expandList: MutableList<String> = mutableListOf()
        if (expandApplicant) {
            expandList.add("applicant")
        }
        if (expandOffer) {
            expandList.add("offer")
        }
        if (expandList.size > 0) {
            uriComponentBuilder.queryParam("expand", expandList.joinToString(separator = ","))
        }
        val response: ResponseEntity<Map<Long, DreamApplyApplicationDto>> = restTemplate.exchange(
            uriComponentBuilder.build().toUri(),
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<Map<Long, DreamApplyApplicationDto>>() {}
        )
        return response.body
    }

    fun getApplicationById(applicationId: Long): DreamApplyApplicationDto? {
        val response: ResponseEntity<DreamApplyApplicationDto> = restTemplate.exchange(
            "${apiUrl}applications/$applicationId",
            HttpMethod.GET,
            httpEntity,
            DreamApplyApplicationDto::class.java
        )
        return response.body
    }

    fun getApplicantById(applicantId: Long): DreamApplyApplicantDto? {
        val response: ResponseEntity<DreamApplyApplicantDto> = restTemplate.exchange(
            "${apiUrl}applicants/$applicantId",
            HttpMethod.GET,
            httpEntity,
            DreamApplyApplicantDto::class.java
        )
        return response.body
    }

    fun getPhoto(photoUrl: String): ByteArray? {
        val responseEntity: ResponseEntity<Resource> = restTemplate.exchange(
            "$instanceUrl$photoUrl",
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

    fun getAcademicTerms(): Map<Long, AcademicTermDto>? {
        val responseEntity: ResponseEntity<Map<Long, AcademicTermDto>> = restTemplate.exchange(
            "${apiUrl}academic-terms",
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<Map<Long, AcademicTermDto>>() {}
        )
        return responseEntity.body
    }

    fun getAcademicTermById(academicTermId: Long): AcademicTermDto? {
        val responseEntity: ResponseEntity<AcademicTermDto> = restTemplate.exchange(
            "${apiUrl}academic-terms/$academicTermId",
            HttpMethod.GET,
            httpEntity,
            AcademicTermDto::class.java
        )
        return responseEntity.body
    }

    fun getCourses(statuses: String? = null, types: String? = null, modes: String? = null): Map<Long, CourseDto>? {
        val uriComponentBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("${apiUrl}courses")
        statuses?.run {
            uriComponentBuilder.queryParam("byStatuses", statuses)
        }
        types?.run {
            uriComponentBuilder.queryParam("byTypes", types)
        }
        modes?.run {
            uriComponentBuilder.queryParam("byModes", modes)
        }
        val response: ResponseEntity<Map<Long, CourseDto>> = restTemplate.exchange(
            uriComponentBuilder.build().toUri(),
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<Map<Long, CourseDto>>() {}
        )
        return response.body
    }

    fun getApplicationOffers(path: String): Map<Long, OfferDto>? {
        val uriComponentBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("$instanceUrl$path")
        val response: ResponseEntity<Map<Long, OfferDto>> = restTemplate.exchange(
            uriComponentBuilder.build().toUri(),
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<Map<Long, OfferDto>>() {}
        )
        return response.body
    }

    fun getAllFlags(): Map<Long, FlagDto>? {
        val uriComponentBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("${apiUrl}applications/flags")
        val response: ResponseEntity<Map<Long, FlagDto>> = restTemplate.exchange(
            uriComponentBuilder.build().toUri(),
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<Map<Long, FlagDto>>() {}
        )
        return response.body
    }

    fun getApplicationFlags(path: String): Map<Long, FlagInfoDto>? {
        val uriComponentBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("$instanceUrl$path")
        val response: ResponseEntity<Map<Long, FlagInfoDto>> = restTemplate.exchange(
            uriComponentBuilder.build().toUri(),
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<Map<Long, FlagInfoDto>>() {}
        )
        return response.body
    }

    fun getApplicantCourse(path: String): Map<Long, ApplicationCourseDto>? {
        val uriComponentBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("$instanceUrl$path")
        val response: ResponseEntity<Map<Long, ApplicationCourseDto>> = restTemplate.exchange(
            uriComponentBuilder.build().toUri(),
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<Map<Long, ApplicationCourseDto>>() {}
        )
        return response.body
    }

    fun getCourseByPath(path: String): CourseDto? {
        val uriComponentBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("$instanceUrl$path")
        val response: ResponseEntity<CourseDto> = restTemplate.exchange(
            uriComponentBuilder.build().toUri(),
            HttpMethod.GET,
            httpEntity,
            CourseDto::class.java
        )
        return response.body
    }

    fun sendEmail(applicantId: Long, emailDto: EmailDto) {
        val httpEntity = HttpEntity<Any>(emailDto, httpHeaders)
        restTemplate.exchange(
            "${apiUrl}applicants/$applicantId/emails",
            HttpMethod.POST,
            httpEntity,
            Any::class.java
        )
    }

    fun getOfferTypes(): Map<String, OfferTypeDto>? {
        val uriComponentBuilder: UriComponentsBuilder =
            UriComponentsBuilder.fromHttpUrl("${apiUrl}applications/offers/types")
        val response: ResponseEntity<Map<String, OfferTypeDto>> = restTemplate.exchange(
            uriComponentBuilder.build().toUri(),
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<Map<String, OfferTypeDto>>() {}
        )
        return response.body
    }

    private fun getUriComponentBuilder() = UriComponentsBuilder.fromHttpUrl("${apiUrl}applications")
}
