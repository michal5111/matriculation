package pl.poznan.ue.matriculation.oracle.service

import org.springframework.stereotype.Service
import pl.poznan.ue.matriculation.local.domain.applicants.ErasmusData
import pl.poznan.ue.matriculation.local.domain.enum.AccommodationPreference
import pl.poznan.ue.matriculation.local.domain.enum.DurationType
import pl.poznan.ue.matriculation.oracle.domain.Arrival
import pl.poznan.ue.matriculation.oracle.domain.DidacticCycle
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.domain.PersonProgramme
import pl.poznan.ue.matriculation.oracle.repo.DidacticCycleRepository
import pl.poznan.ue.matriculation.oracle.repo.SchoolRepository
import java.util.*

@Service
class ErasmusService(
    private val schoolRepository: SchoolRepository,
    private val didacticCycleRepository: DidacticCycleRepository
) {

    fun createArrival(
        person: Person,
        erasmusData: ErasmusData,
        didacticCycleCode: String,
        didacticCycle: DidacticCycle,
        didacticCycleYear: DidacticCycle,
        personProgramme: PersonProgramme,
        startDate: Date
    ): Arrival {
        return Arrival(
            person = person,
            wantAccommodation = if (erasmusData.accommodationPreference == AccommodationPreference.DORMITORY) 'T'
            else 'N',
            personProgramme = personProgramme,
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