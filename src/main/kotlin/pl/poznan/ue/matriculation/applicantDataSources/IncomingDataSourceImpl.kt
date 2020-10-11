package pl.poznan.ue.matriculation.applicantDataSources

import pl.poznan.ue.matriculation.dreamApply.dto.applicant.DreamApplyApplicantDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.DreamApplyApplicationDto
import pl.poznan.ue.matriculation.dreamApply.mapper.DreamApplyApplicantMapper
import pl.poznan.ue.matriculation.dreamApply.mapper.DreamApplyApplicationMapper
import pl.poznan.ue.matriculation.dreamApply.service.DreamApplyService
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.ProgrammeDto

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

    val programmePattern = "^([SNsn])([123])-\\w*".toRegex()

    override fun getApplicationsPage(import: Import, registrationCode: String, programmeForeignId: String, pageNumber: Int): IPage<DreamApplyApplicationDto> {
        val programmeForeignIdSplit = programmeForeignId.split(";")
        val programmeId = programmeForeignIdSplit[0]
        val flagId = programmeForeignIdSplit[1]
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
                        "byFlagIDs" to flagId
                )
        ) ?: throw java.lang.IllegalArgumentException("Unable to get applicants")
        val applications = applicationMap.values.filter { dreamApplyApplicationDto ->
            val applicationOffers = dreamApplyService.getApplicationOffers(dreamApplyApplicationDto.offers)
            applicationOffers!!.any {
                it.value.course == "/api/courses/$programmeId"
                        && it.value.type == status
            }
        }.filter { dreamApplyApplicationDto ->
            val applicationFlags = dreamApplyService.getApplicationFlags(dreamApplyApplicationDto.flags)
            applicationFlags!!.any {
                it.key == semesterFlagId
            }
        }
        return object : IPage<DreamApplyApplicationDto> {
            override fun getSize(): Int {
                return applications.size
            }

            override fun getResultsList(): List<DreamApplyApplicationDto> {
                return applications
            }

            override fun hasNext(): Boolean {
                return false
            }
        }
    }

    override fun getAvailableRegistrationProgrammes(registration: String): List<ProgrammeDto> {
        val courses = dreamApplyService.getCourses(statuses = "Online")
                ?: throw IllegalStateException("Unable to get courses list.")
        var flags = dreamApplyService.getAllFlags()
                ?: throw java.lang.IllegalStateException("Unable to get flags list.")
        flags = flags.filter {
            it.value.name.matches(programmePattern)
        }
        val programmeList = mutableListOf<ProgrammeDto>()
        courses.values.forEach { courseDto ->
            flags.forEach { flagMapEntry ->
                programmeList.add(
                        ProgrammeDto(
                                id = "${courseDto.id};${flagMapEntry.key}",
                                name = "${courseDto.name} ${flagMapEntry.value.name}",
                                usosId = flagMapEntry.value.name
                        )
                )
            }
        }
        return programmeList
    }

    override fun preprocess(applicationDto: DreamApplyApplicationDto, applicantDto: DreamApplyApplicantDto) {
        super.preprocess(applicationDto, applicantDto).also {
            val applicationCourses = dreamApplyService.getApplicantCourse(applicationDto.courses)
            val course = applicationCourses?.values?.first()?.course?.let { applicationCourse ->
                return@let dreamApplyService.getCourseByPath(applicationCourse)
            }
            applicationDto.courseType = course?.type
            applicationDto.duration = course?.duration
        }
    }
}