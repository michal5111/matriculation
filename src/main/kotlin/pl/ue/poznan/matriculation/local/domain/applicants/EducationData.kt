package pl.ue.poznan.matriculation.local.domain.applicants


import pl.ue.poznan.matriculation.irk.domain.applicants.EducationData
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
        constructor(educationData: EducationData): this(
                documents = educationData.documents
                        .stream()
                        .map { document -> Document(document) }
                        .collect(Collectors.toList()),
                highSchoolCity = educationData.highSchoolCity,
                highSchoolName = educationData.highSchoolName,
                highSchoolType = educationData.highSchoolType,
                highSchoolUsosCode = educationData.highSchoolUsosCode
        )
}