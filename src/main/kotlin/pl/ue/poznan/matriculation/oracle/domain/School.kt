package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.validator.constraints.URL
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email

@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Entity
@Table(name = "DZ_SZKOLY")
data class School(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_SZK_SEQ")
        @SequenceGenerator(sequenceName = "DZ_SZK_SEQ", allocationSize = 1, name = "DZ_SZK_SEQ")
        @Column(name = "ID")
        val id: Long,

        @Column(name = "NAZWA")
        val name: String,

        @Column(name = "RODZAJ_SZKOLY")
        val schoolType: String,

        @Column(name = "STATUS_PRAWNY")
        val legalStatus: String?,

        @Column(name = "STATUS_PRAWNY_ANG")
        val legalStatusEng: String?,

        @Column(name = "KOD_SZK")
        val schoolCode: String?,

        @URL
        @Column(name = "WWW")
        val www: String?,

        @Column(name = "KODERASMUS")
        val erasmusCode: String?,

        @Column(name = "NAME")
        val nameEng: String?,

        @Column(name = "MOD_DATA")
        val modificationDate: Date,

        @Column(name = "MOD_ID")
        val modificationUser: String,

        @Column(name = "UTW_DATA")
        val creationDate: Date,

        @Column(name = "UTW_ID")
        val creationUser: String,

        @Column(name = "KOD_POLON")
        val polonCode: String?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "JED_NADRZ_ID", referencedColumnName = "ID")
        val parentSchool: School?,

        @Column(name = "KOD_HR")
        val hrCode: String?,

        @Column(name = "SKALA_OCEN")
        val gradingScale: String?,

        @Column(name = "SKALA_OCEN_ANG")
        val gradingScaleEng: String?,

        @Email
        @Column(name = "EMAIL")
        val email: String?,

        @Column(name = "PIC")
        val PIC: Int?,

        @Column(name = "CZY_PODMIOT_PUBLICZNY")
        val isPublicEntity: String?,

        @Column(name = "CZY_NON_PROFIT")
        val isNonProfit: String?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "LOGO_BLOB_ID", referencedColumnName = "ID")
        val logoBlob: Blob?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "REG_NUTS_KOD", referencedColumnName = "KOD")
        val nutsRegion: NUTSRegion?,

        @Column(name = "SCHAC")
        val schac: String?,

        @Column(name = "INFOKARTA_ID_BLOBBOX")
        val factSheetId: String?,

        @Column(name = "INFOKARTA_UWAGI")
        val factSheetComments: String?,

        @URL
        @Column(name = "INFOKARTA_WWW")
        val factSheetWWW: String?,

        @Column(name = "CZY_WYSWIETLAC")
        val display: String,

        @Email
        @Column(name = "EMAIL_NOMINACJE")
        val emailNominations: String?,

        @JsonIgnore
        @OneToMany(mappedBy = "middleSchool", fetch = FetchType.EAGER)
        val Persons: Set<Person>
        )