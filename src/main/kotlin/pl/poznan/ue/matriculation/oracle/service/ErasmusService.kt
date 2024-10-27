package pl.poznan.ue.matriculation.oracle.service

import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.local.domain.applicants.ErasmusData
import pl.poznan.ue.matriculation.local.domain.enum.AccommodationPreference
import pl.poznan.ue.matriculation.local.domain.enum.DurationType
import pl.poznan.ue.matriculation.oracle.domain.Arrival
import pl.poznan.ue.matriculation.oracle.domain.DidacticCycle
import pl.poznan.ue.matriculation.oracle.repo.DidacticCycleRepository
import pl.poznan.ue.matriculation.oracle.repo.SchoolRepository
import java.time.LocalDate

@Service
class ErasmusService(
    private val schoolRepository: SchoolRepository,
    private val didacticCycleRepository: DidacticCycleRepository
) {

    fun createArrival(
        erasmusData: ErasmusData,
        didacticCycleCode: String,
        didacticCycle: DidacticCycle,
        didacticCycleYear: DidacticCycle,
        startDate: LocalDate
    ): Arrival {
        return Arrival(
            wantAccommodation = erasmusData.accommodationPreference == AccommodationPreference.DORMITORY,
            didacticCycleAcademicYear = didacticCycleYear,
            startDate = startDate,
            financingDidacticCycleAcademicYear = didacticCycleYear,
            school = erasmusData.homeInstitution?.erasmusCode?.let { erasmusCode ->
                schoolRepository.findSchoolByErasmusCode(erasmusCode)
            },
            arrivalType = when (erasmusData.type) {
                "Studies" -> "S"
                else -> "S"
            },
            endDate = if (erasmusData.duration == DurationType.ONE_SEMESTER) didacticCycle.endDate
            else didacticCycleRepository.getNextDidacticCycleEndDate(didacticCycleCode)
                ?: throw IllegalStateException("Nie można znaleźć cyklu dydaktycznego"),
            stayTimePlan = when {
                erasmusData.duration == DurationType.TWO_SEMESTERS -> 'R'
                didacticCycle.description.startsWith("semestr letni") -> 'L'
                didacticCycle.description.startsWith("semestr zimowy") -> 'Z'
                else -> 'I'
            }
        )
    }
}
