package pl.poznan.ue.matriculation.local.repo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import pl.poznan.ue.matriculation.AbstractIT
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import java.time.LocalDate
import kotlin.test.assertEquals

class ApplicantRepositoryTest : AbstractIT() {

    @Autowired
    private lateinit var applicantRepository: ApplicantRepository;

    @Test
    fun shouldFindByForeignIdAndDataSourceId() {
        //given
        val applicant = Applicant(
            foreignId = 1,
            dataSourceId = "test",
            email = "email",
            dateOfBirth = LocalDate.now(),
            family = "family",
            given = "given",
            sex = 'K'
        )
        applicantRepository.save(applicant)
        //when
        val applicant2 = applicantRepository.findByForeignIdAndDataSourceId(1, "test")
        //should
        assertEquals(applicant, applicant2)
    }
}
