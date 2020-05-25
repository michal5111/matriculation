package pl.poznan.ue.matriculation.oracle.domain

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "DZ_ZGLOSZENIA_REKRUTACYJNE")
class IrkApplication(

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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IrkApplication

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}