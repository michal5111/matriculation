package pl.poznan.ue.matriculation.dreamApply.service

import org.apache.commons.io.IOUtils
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import pl.poznan.ue.matriculation.dreamApply.dto.academicTerms.AcademicTermDto
import pl.poznan.ue.matriculation.dreamApply.dto.applicant.ApplicantDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.ApplicationDto

class DreamApplyService(
        val instanceUrl: String,
        private val apiKey: String
) {
    private val restTemplate: RestTemplate = RestTemplate()

    private val apiUrl = "$instanceUrl/api/"

    private class LongApplicationMapResult : ParameterizedTypeReference<Map<Long, ApplicationDto>>()
    private class LongAcademicTermMapResult : ParameterizedTypeReference<Map<Long, AcademicTermDto>>()

    fun getApplicationsCountByFilter(academicTermID: String, academicYear: String, additionalFilters: Map<String, String>?): Long {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "DREAM apikey=\"$apiKey\"")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val uriComponentBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("${apiUrl}applications")
        uriComponentBuilder.queryParam("byAcademicTermID", academicTermID)
        uriComponentBuilder.queryParam("byAcademicYear", academicYear)
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

    fun getApplicationsByFilter(academicTermID: String, academicYear: String, additionalFilters: Map<String, String>? = null): Map<Long, ApplicationDto>? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "DREAM apikey=\"$apiKey\"")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val uriComponentBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("${apiUrl}applications")
        uriComponentBuilder.queryParam("byAcademicTermID", academicTermID)
        uriComponentBuilder.queryParam("byAcademicYear", academicYear)
        additionalFilters?.forEach { (filterName, filterValue) ->
            uriComponentBuilder.queryParam(filterName, filterValue)
        }
        val response: ResponseEntity<Map<Long, ApplicationDto>> = restTemplate.exchange(
                uriComponentBuilder.build().toUri(),
                HttpMethod.HEAD,
                httpEntity,
                LongApplicationMapResult()
        )
        return response.body
    }

    fun getApplicationById(applicationId: Long): ApplicationDto? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "DREAM apikey=\"$apiKey\"")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<ApplicationDto> = restTemplate.exchange(
                "${apiUrl}applications/$applicationId",
                HttpMethod.HEAD,
                httpEntity,
                ApplicationDto::class.java
        )
        return response.body
    }

    fun getApplicantById(applicantId: Long): ApplicantDto? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.set("Authorization", "DREAM apikey=\"$apiKey\"")
        val httpEntity: HttpEntity<Any> = HttpEntity(httpHeaders)
        val response: ResponseEntity<ApplicantDto> = restTemplate.exchange(
                "${apiUrl}applicants/$applicantId",
                HttpMethod.HEAD,
                httpEntity,
                ApplicantDto::class.java
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
                "${apiUrl}academic-terms",
                HttpMethod.GET,
                httpEntity,
                AcademicTermDto::class.java
        )
        return responseEntity.body
    }
}