package pl.ue.poznan.matriculation.local.domain.applicants


import pl.ue.poznan.matriculation.irk.dto.applicants.EducationDataDTO
import java.util.stream.Collectors
import javax.persistence.*

@Entity
data class EducationData(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,
        @OneToMany
        val documents: List<Document>,
        val highSchoolCity: String?,
        val highSchoolName: String?,
        val highSchoolType: String?,
        val highSchoolUsosCode: String?
) {
        constructor(educationDataDTO: EducationDataDTO): this(
                documents = educationDataDTO.documentDTOS
                        .stream()
                        .map { document -> Document(document) }
                        .collect(Collectors.toList()),
                highSchoolCity = educationDataDTO.highSchoolCity,
                highSchoolName = educationDataDTO.highSchoolName,
                highSchoolType = educationDataDTO.highSchoolType,
                highSchoolUsosCode = educationDataDTO.highSchoolUsosCode
        )
}