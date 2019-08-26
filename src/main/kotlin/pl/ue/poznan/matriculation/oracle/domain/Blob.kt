package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*
import javax.persistence.*

@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Entity
@Table(name = "DZ_BLOBY")
data class Blob(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_BLOB_SEQ")
        @SequenceGenerator(sequenceName = "DZ_BLOB_SEQ", allocationSize = 1, name = "DZ_BLOB_SEQ")
        @Column(name = "ID", length = 10)
        val id: Long,

        @Column(name = "MOD_DATA", nullable = false)
        val modificationDate: Date,

        @Column(name = "MOD_ID", length = 30, nullable = false)
        val modificationUser: String,

        @Column(name = "UTW_DATA", nullable = false)
        val creationDate: Date,

        @Column(name = "UTW_ID", length = 30, nullable = false)
        val creationUser: String,

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
        )