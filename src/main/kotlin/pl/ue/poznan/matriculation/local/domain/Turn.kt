package pl.ue.poznan.matriculation.local.domain


import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Turn(
        @Id
        @Column(name = "date_from")
        val dateFrom: Date?,

        @Id
        @Column(name = "date_to")
        val dateTo: Date?,

        @Id
        val programme: String?,

        @Id
        val registration: String?

//        @OneToOne(mappedBy = "turn", fetch = FetchType.LAZY)
//        var application: Application? = null
) : Serializable