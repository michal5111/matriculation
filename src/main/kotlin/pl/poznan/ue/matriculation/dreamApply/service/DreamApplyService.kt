package pl.poznan.ue.matriculation.dreamApply.service

import org.apache.commons.io.IOUtils
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import pl.poznan.ue.matriculation.dreamApply.dto.academicTerms.AcademicTermDto
import pl.poznan.ue.matriculation.dreamApply.dto.academicTerms.CourseDto
import pl.poznan.ue.matriculation.dreamApply.dto.applicant.DreamApplyApplicantDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.DreamApplyApplicationDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.OfferDto

class DreamApplyService(
        val instanceUrl: String,
        private val apiKey: String
) {
    private val restTemplate: RestTemplate = RestTemplate()

    private val apiUrl = "$instanceUrl/api/"

    private class LongApplicationMapResult : ParameterizedTypeReference<Map<Long, DreamApplyApplicationDto>>()
    private class LongAcademicTermMapResult : ParameterizedTypeReference<Map<Long, AcademicTermDto>>()
    private class LongCourseMapResult : ParameterizedTypeReference<Map<Long, CourseDto>>()
    private class LongOfferDtoResult : ParameterizedTypeReference<Map<Long, OfferDto>>()

    fun getApplicationsCountByFilter(academicTermID: String? = null, academicYear: String? = null, additionalFilters: Map<String, String>?): Long {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "DREAM apikey=\"$apiKey\"")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val uriComponentBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("${apiUrl}applications")
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

    fun getApplicationsByFilter(academicTermID: String? = null, academicYear: String? = null, additionalFilters: Map<String, String>? = null): Map<Long, DreamApplyApplicationDto>? {
        if (academicTermID == null && academicYear == null) {
            throw IllegalArgumentException("AcademicTerm adn academicYear are null")
        }
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "DREAM apikey=\"$apiKey\"")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val uriComponentBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("${apiUrl}applications")
        academicTermID?.run {
            uriComponentBuilder.queryParam("byAcademicTermID", academicTermID)
        }
        academicYear?.run {
            uriComponentBuilder.queryParam("byAcademicYear", academicYear)
        }
        additionalFilters?.forEach { (filterName, filterValue) ->
            uriComponentBuilder.queryParam(filterName, filterValue)
        }
        println(uriComponentBuilder.build().toUri())
        val response: ResponseEntity<Map<Long, DreamApplyApplicationDto>> = restTemplate.exchange(
                uriComponentBuilder.build().toUri(),
                HttpMethod.GET,
                httpEntity,
                LongApplicationMapResult()
        )
        return response.body
    }

    fun getApplicationById(applicationId: Long): DreamApplyApplicationDto? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "DREAM apikey=\"$apiKey\"")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<DreamApplyApplicationDto> = restTemplate.exchange(
                "${apiUrl}applications/$applicationId",
                HttpMethod.GET,
                httpEntity,
                DreamApplyApplicationDto::class.java
        )
        return response.body
    }

    fun getApplicantById(applicantId: Long): DreamApplyApplicantDto? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "DREAM apikey=\"$apiKey\"")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<DreamApplyApplicantDto> = restTemplate.exchange(
                "${apiUrl}applicants/$applicantId",
                HttpMethod.GET,
                httpEntity,
                DreamApplyApplicantDto::class.java
        )
        return response.body
    }

    fun getPhoto(photoUrl: String): ByteArray {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "DREAM apikey=\"$apiKey\"")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val responseEntity: ResponseEntity<Resource> = restTemplate.exchange(
                "$instanceUrl$photoUrl",
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

    fun getAcademicTerms(): Map<Long, AcademicTermDto>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "DREAM apikey=\"$apiKey\"")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val responseEntity: ResponseEntity<Map<Long, AcademicTermDto>> = restTemplate.exchange(
                "${apiUrl}academic-terms",
                HttpMethod.GET,
                httpEntity,
                LongAcademicTermMapResult()
        )
        return responseEntity.body
    }

    fun getAcademicTermById(academicTermId: Long): AcademicTermDto? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "DREAM apikey=\"$apiKey\"")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val responseEntity: ResponseEntity<AcademicTermDto> = restTemplate.exchange(
                "${apiUrl}academic-terms/$academicTermId",
                HttpMethod.GET,
                httpEntity,
                AcademicTermDto::class.java
        )
        return responseEntity.body
    }

    fun getCourses(statuses: String? = null, types: String? = null, modes: String? = null): Map<Long, CourseDto>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "DREAM apikey=\"$apiKey\"")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
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
                LongCourseMapResult()
        )
        return response.body
    }

    fun getApplicationOffers(path: String): Map<Long, OfferDto>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "DREAM apikey=\"$apiKey\"")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val uriComponentBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("$instanceUrl$path")
        val response: ResponseEntity<Map<Long, OfferDto>> = restTemplate.exchange(
                uriComponentBuilder.build().toUri(),
                HttpMethod.GET,
                httpEntity,
                LongOfferDtoResult()
        )
        return response.body
    }
}