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
        @GeneratedValue
        @Column(name = "ID")
        val id: Long,

        @Column(name = "MOD_DATA")
        val modificationDate: Date,

        @Column(name = "MOD_ID")
        val modificationUser: String,

        @Column(name = "UTW_DATA")
        val creationDate: Date,

        @Column(name = "UTW_ID")
        val creationUser: String,

        @JsonIgnore
        @Lob
        @Column(name = "OBIEKT")
        val blob: ByteArray,

        @Column(name = "KATEGORIA")
        val category: String?,

        @Column(name = "OPIS")
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