package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "DZ_STOPNIE_ZAWODOWE")
data class ProfessionalDegree(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_STZA_SEQ")
        @SequenceGenerator(sequenceName = "DZ_STZA_SEQ", allocationSize = 1, name = "DZ_STZA_SEQ")
        @Column(name = "ID", length = 10)
        val id: Long? = null,

        @Column(name = "KOD", length = 20, nullable = true)
        val code: String,

        @Column(name = "NAZWA", length = 100, nullable = false)
        var name: String,

        @Column(name = "NAZWA_W_DOPELNIACZU", length = 100, nullable = true)
        var genitiveName: String? = null,

        @Column(name = "KOD_POLON", length = 50, nullable = false)
        var polonCode: String,

        @JsonIgnore
        @OneToMany(mappedBy = "professionalDegree", fetch = FetchType.LAZY)
        val fieldOfStudyPermissions: MutableList<FieldOfStudyPermission>,

        @JsonIgnore
        @OneToMany(mappedBy = "professionalDegree", fetch = FetchType.LAZY)
        val conductedFieldOfStudy: MutableList<ConductedFieldOfStudy>
)