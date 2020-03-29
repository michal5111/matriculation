package pl.ue.poznan.matriculation.local.domain.import

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.ue.poznan.matriculation.local.domain.applications.Application
import java.util.*
import javax.persistence.*

@Entity
data class Import(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        val programmeCode: String,

        val stageCode: String,

        val registration: String,

        val indexPoolCode: String,

        val startDate: Date,

        val dateOfAddmision: Date,

        val didacticCycleCode: String,

        @JsonIgnore
        @OneToMany(mappedBy = "import", fetch = FetchType.EAGER, cascade = [CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE])
        val applications: MutableList<Application> = mutableListOf(),

        @OneToOne(mappedBy = "import", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        var importProgress: ImportProgress? = null
) {
        fun addApplication(application: Application) {
                application.import = this
                applications.add(application)
        }

        fun addAllApplications(applications: List<Application>) {
                applications.forEach {
                        it.import = this
                        this.applications.add(it)
                }
        }
}