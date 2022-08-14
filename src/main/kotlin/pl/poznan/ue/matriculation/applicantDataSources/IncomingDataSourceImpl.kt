package pl.poznan.ue.matriculation.applicantDataSources

import pl.poznan.ue.matriculation.dreamApply.dto.applicant.DreamApplyApplicantDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.DreamApplyApplicationDto
import pl.poznan.ue.matriculation.dreamApply.mapper.DreamApplyApplicantMapper
import pl.poznan.ue.matriculation.dreamApply.mapper.DreamApplyApplicationMapper
import pl.poznan.ue.matriculation.dreamApply.service.DreamApplyService
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.DataSourceAdditionalParameter
import pl.poznan.ue.matriculation.local.dto.ProgrammeDto
import pl.poznan.ue.matriculation.local.dto.SelectionListDataSourceAdditionalParameter
import pl.poznan.ue.matriculation.local.dto.SelectionListValue

class IncomingDataSourceImpl(
    name: String,
    applicantMapper: DreamApplyApplicantMapper,
    applicationMapper: DreamApplyApplicationMapper,
    val status: String,
    val dreamApplyService: DreamApplyService,
    id: String
) : DreamApplyDataSourceImpl(
    name = name,
    applicantMapper = applicantMapper,
    applicationMapper = applicationMapper,
    status = status,
    dreamApplyService = dreamApplyService,
    id = id
) {
    companion object {
        private val PROGRAMME_PATTERN = "^([SN])([123])-\\w*".toRegex()
    }

    override val additionalParameters: List<DataSourceAdditionalParameter>
        get() = listOf(
            SelectionListDataSourceAdditionalParameter("Kurs", null) {
                val courses = dreamApplyService.getCourses(statuses = "Online", types = "Studies", modes = "FT")
                    ?: throw IllegalStateException("Unable to get courses list.")
                courses.map {
                    SelectionListValue(it.value.name, it.value.id.toString())
                }
            }
        )

    override fun getApplicationsPage(
        import: Import,
        registrationCode: String,
        programmeForeignId: String,
        pageNumber: Int
    ): IPage<DreamApplyApplicationDto> {
        val programmeId = import.additionalProperties?.get("Kurs") as String
        val semester = import.stageCode.substring(0, 2)
        val semesterFlagId = dreamApplyService.getAllFlags()?.filter {
            it.value.name == "${semester[0]}-${semester[1]}"
        }?.map {
            it.key
        }?.firstOrNull()
        val applicationMap = dreamApplyService.getApplicationsByFilter(
            academicTermID = registrationCode,
            additionalFilters = mapOf(
                "byCourseIDs" to programmeId,
                "byOfferTypes" to status,
                "byFlagIDs" to programmeForeignId
            )
        ) ?: throw java.lang.IllegalArgumentException("Unable to get applicants")
        val applications = applicationMap.values.filter { dreamApplyApplicationDto ->
            val applicationOffers = dreamApplyService.getApplicationOffers(dreamApplyApplicationDto.offers)
            val applicationFlags = dreamApplyService.getApplicationFlags(dreamApplyApplicationDto.flags)
            applicationOffers!!.any {
                it.value.course == "/api/courses/$programmeId"
                    && it.value.type == status
            } && applicationFlags!!.any {
                it.key == semesterFlagId
            }
        }
        return object : IPage<DreamApplyApplicationDto> {
            override fun getTotalSize(): Int {
                return applications.size
            }

            override fun getContent(): List<DreamApplyApplicationDto> {
                return applications
            }

            override fun hasNext(): Boolean {
                return false
            }
        }
    }

    override fun getAvailableRegistrationProgrammes(registration: String): List<ProgrammeDto> {
        var flags = dreamApplyService.getAllFlags()
            ?: throw java.lang.IllegalStateException("Unable to get flags list.")
        flags = flags.filter {
            it.value.name.matches(PROGRAMME_PATTERN)
        }
        val programmeList = flags.map { (key, value) ->
            ProgrammeDto(
                id = key.toString(),
                name = value.name,
                usosId = value.name
            )
        }
        return programmeList
    }

    override fun getApplicantById(applicantId: Long, applicationDto: DreamApplyApplicationDto): DreamApplyApplicantDto {
        return super.getApplicantById(applicantId, applicationDto).also {
            val applicationCourses = dreamApplyService.getApplicantCourse(applicationDto.courses)
            val course = applicationCourses?.values?.first()?.course?.let { applicationCourse ->
                dreamApplyService.getCourseByPath(applicationCourse)
            }
            applicationDto.courseType = course?.type
            applicationDto.duration = course?.duration
        }
    }
}
