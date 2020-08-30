package pl.poznan.ue.matriculation.dreamApply.mapper

import pl.poznan.ue.matriculation.dreamApply.dto.applicant.DreamApplyApplicantDto
import pl.poznan.ue.matriculation.dreamApply.dto.application.HomeDto
import pl.poznan.ue.matriculation.local.domain.applicants.*
import pl.poznan.ue.matriculation.local.domain.enum.AccommodationPreference
import pl.poznan.ue.matriculation.local.domain.enum.DurationType

class IncomingApplicantMapper : DreamApplyApplicantMapper() {

    override fun map(dreamApplyApplicantDto: DreamApplyApplicantDto): Applicant {
        return super.map(dreamApplyApplicantDto).also {
            val homeData = dreamApplyApplicantDto.dreamApplyApplication?.home
            it.erasmusData = createErasmusData(homeData!!, it, dreamApplyApplicantDto)
        }
    }

    override fun update(applicant: Applicant, dreamApplyApplicantDto: DreamApplyApplicantDto): Applicant {
        return super.update(applicant, dreamApplyApplicantDto).also {
            val accommodationPreference = getAccommodationPreference(dreamApplyApplicantDto)
            val homeData = dreamApplyApplicantDto.dreamApplyApplication?.home!!
            if (it.erasmusData == null) {
                it.erasmusData = createErasmusData(homeData, it, dreamApplyApplicantDto)
            } else {
                it.erasmusData!!.apply {
                    this.accommodationPreference = accommodationPreference
                    coordinatorData = CoordinatorData(
                            email = homeData.contact?.email,
                            name = homeData.contact?.name,
                            phone = homeData.contact?.telephone?.fixed
                    )
                    courseData = CourseData(
                            level = homeData.course?.level,
                            name = homeData.course?.name,
                            term = homeData.course?.term
                    )
                    homeInstitution = HomeInstitution(
                            address = homeData.institution?.address,
                            country = homeData.institution?.country,
                            departmentName = homeData.institution?.department?.name,
                            erasmusCode = homeData.institution?.erasmus
                    )
                    type = dreamApplyApplicantDto.dreamApplyApplication!!.courseType
                    duration = when (dreamApplyApplicantDto.dreamApplyApplication?.duration) {
                        "2 semesters" -> DurationType.TWO_SEMESTERS
                        else -> DurationType.ONE_SEMESTER
                    }
                }
            }
        }
    }

    private fun getAccommodationPreference(dreamApplyApplicantDto: DreamApplyApplicantDto): AccommodationPreference {
        return dreamApplyApplicantDto.dreamApplyApplication?.extras?.find { extraDto ->
            extraDto.name == "Accommodation preference"
        }.let { extraDto ->
            return@let when (extraDto?.name) {
                "I will book a private accommodation" -> AccommodationPreference.PRIVATE
                "I would like to apply for a place in the University dormitory (double room)" -> AccommodationPreference.DORMITORY
                else -> AccommodationPreference.NONE
            }
        }
    }

    private fun createErasmusData(homeDto: HomeDto, applicant: Applicant, dreamApplyApplicantDto: DreamApplyApplicantDto): ErasmusData {
        val accommodationPreference = getAccommodationPreference(dreamApplyApplicantDto)
        val erasmusData = ErasmusData(
                applicant = applicant,
                accommodationPreference = accommodationPreference,
                coordinatorData = CoordinatorData(
                        email = homeDto.contact?.email,
                        name = homeDto.contact?.name,
                        phone = homeDto.contact?.telephone?.fixed
                ),
                courseData = CourseData(
                        level = homeDto.course?.level,
                        name = homeDto.course?.name,
                        term = homeDto.course?.term
                ),
                homeInstitution = HomeInstitution(
                        address = homeDto.institution?.address,
                        country = homeDto.institution?.country,
                        departmentName = homeDto.institution?.department?.name,
                        erasmusCode = homeDto.institution?.erasmus
                ),
                type = dreamApplyApplicantDto.dreamApplyApplication?.courseType,
                duration = when (dreamApplyApplicantDto.dreamApplyApplication?.duration) {
                    "2 semesters" -> DurationType.TWO_SEMESTERS
                    else -> DurationType.ONE_SEMESTER
                }
        )
        erasmusData.coordinatorData?.erasmusData = applicant.erasmusData
        erasmusData.courseData?.erasmusData = applicant.erasmusData
        erasmusData.homeInstitution?.erasmusData = applicant.erasmusData
        return erasmusData
    }
}