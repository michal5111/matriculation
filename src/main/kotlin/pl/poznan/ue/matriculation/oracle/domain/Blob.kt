package pl.poznan.ue.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "DZ_BLOBY")
class Blob(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_BLOB_SEQ")
        @SequenceGenerator(sequenceName = "DZ_BLOB_SEQ", allocationSize = 1, name = "DZ_BLOB_SEQ")
        @Column(name = "ID", length = 10)
        val id: Long? = null,

//        @Column(name = "MOD_DATA", nullable = false)
//        val modificationDate: Date,
//
//        @Column(name = "MOD_ID", length = 30, nullable = false)
//        val modificationUser: String,
//
//        @Column(name = "UTW_DATA", nullable = false)
//        val creationDate: Date,
//
//        @Column(name = "UTW_ID", length = 30, nullable = false)
//        val creationUser: String,

        @Basic(fetch = FetchType.LAZY)
        @JsonIgnore
        @Lob
        @Column(name = "OBIEKT", nullable = false)
        val blob: ByteArray,

        @Column(name = "KATEGORIA", length = 100, nullable = true)
        val category: String?,

        @Column(name = "OPIS", length = 1000, nullable = false)
        val description: String?,

        @JsonIgnore
        @OneToMany(mappedBy = "pictureBlob", fetch = FetchType.LAZY)
        val organizationalUnitPictures: Set<OrganizationalUnit>,

        @JsonIgnore
        @OneToMany(mappedBy = "logoBlob", fetch = FetchType.LAZY)
        val organizationalUnitLogos: Set<OrganizationalUnit>,

        @JsonIgnore
        @OneToMany(mappedBy = "logoBlob", fetch = FetchType.LAZY)
        val schoolLogo: Set<School>
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Blob

                if (id != other.id) return false

                return true
        }

        override fun hashCode(): Int {
                return id?.hashCode() ?: 0
        }

}