package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "DZ_ZDJECIA_OSOB")
data class PersonPhoto(

        @Id
        var id: Long? = null,

        @MapsId
        @JsonIgnore
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = false)
        var person: Person? = null,

        @JsonIgnore
        @Basic(fetch = FetchType.LAZY)
        @Column(name = "FOTO")
        @Lob
        var photoBlob: ByteArray
): Serializable {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as PersonPhoto

                if (id != other.id) return false
                if (person != other.person) return false

                return true
        }

        override fun hashCode(): Int {
                var result = id?.hashCode() ?: 0
                result = 31 * result + (person?.hashCode() ?: 0)
                return result
        }
}