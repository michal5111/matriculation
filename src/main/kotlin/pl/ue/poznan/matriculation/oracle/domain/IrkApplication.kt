package pl.ue.poznan.matriculation.oracle.domain

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "DZ_ZGLOSZENIA_REKRUTACYJNE")
data class IrkApplication(

        @Id
        val id: Long? = null,

        @MapsId
        @OneToOne
        @JoinColumn(name = "PRGOS_ID", referencedColumnName = "ID")
        var personProgramme: PersonProgramme? = null,

        @Column(name = "ID_ZGLOSZENIA")
        val applicationId: Long,

        @Column(name = "POTW_STATUS")
        var confirmationStatus: Int,

        @Column(name = "ADRES_IRK")
        var irkInstance: String,

        @Column(name = "MOD_DATA")
        var modificationDate: Date = Date()
)