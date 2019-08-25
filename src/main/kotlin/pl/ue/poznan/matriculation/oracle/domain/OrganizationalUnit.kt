package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.validator.constraints.pl.REGON
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email

@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Entity
@Table(name = "DZ_JEDNOSTKI_ORGANIZACYJNE")
data class OrganizationalUnit(
        @Id
        @Column(name = "KOD")
        val code: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TJEDN_KOD", referencedColumnName = "KOD")
        val unitType: UnitType,

        @Column(name = "OPIS")
        val description: String,

        @Column(name = "OPIS_ANG")
        val descriptionEng: String?,

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "JED_ORG_KOD", referencedColumnName = "KOD")
        val organizationalUnit: OrganizationalUnit?,

        @Column(name = "CZY_DYDAKTYCZNA")
        val isDidactic: String,

        @Column(name = "CZY_ZATRUDNIA")
        val doesEmploys: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "INST_WWW_KOD", referencedColumnName = "KOD")
        val wwwInstance: WwwInstance?,

        @Column(name = "MOD_DATA")
        val modificationDate: Date,

        @Column(name = "MOD_ID")
        val modificationUser: String,

        @Column(name = "UTW_DATA")
        val creationDate: Date,

        @Column(name = "UTW_ID")
        val creationUser: String,

        @Column(name = "CZY_PRZYZNAJE_AKADEMIKI")
        val isGrantingDorms: String,

        @Column(name = "SKROT_NAZWY")
        val nameAbbreviation: String?,

        @Column(name = "OPIS_DO_SUPLEMENTU")
        val supplementDescription: String?,

        @Column(name = "OPIS_DO_SUPLEMENTU_ANG")
        val supplementDescriptionEng: String?,

        @Column(name = "ADRES_WWW")
        val wwwAddress: String?,

        @Column(name = "CZY_WYSWIETLAC")
        val display: String,

        @Column(name = "CZY_AKTYWNA")
        val isActive: String,

        @Column(name = "OPIS_NIE")
        val germanDescription: String?,

        @Column(name = "OPIS_ROS")
        val russianDescription: String?,

        @Column(name = "OPIS_HIS")
        val hispanicDescription: String?,

        @Column(name = "OPIS_FRA")
        val frenchDescription: String?,

        @Column(name = "CZY_ARCHIWIZUJE")
        val isArchiving: String,

        @Column(name = "ARCH_NR_DOPLYWU_S")
        val archInflowNumberS: Int?,

        @Column(name = "ARCH_NR_DOPLYWU_D")
        val archInflowNumberD: Int?,

        @Column(name = "ARCH_NR_DOPLYWU_H")
        val archInflowNumberH: Int?,

        @Column(name = "ARCH_NR_DOPLYWU_P")
        val archInflowNumberP: Int?,

        @Column(name = "DATA_ZALOZENIA")
        val dateOfEstablishment: Date?,

        @REGON
        @Column(name = "REGON")
        val regon: String?,

        @Column(name = "CZY_ZAMIEJSCOWA")
        val isForeign: String,

        @Column(name = "CZY_PODSTAWOWA")
        val isBasic: String,

        @Column(name = "KOD_POLON")
        val polonCode: String?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "ZDJECIE_BLOB_ID", referencedColumnName = "ID")
        val pictureBlob: Blob?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "LOGO_BLOB_ID", referencedColumnName = "ID")
        val logoBlob: Blob?,

        @Column(name = "MPK")
        val mpk: String?,

        @Column(name = "SKROT_DO_JRWA")
        val jrwaAbbreviation: String?,

        @Email
        @Column(name = "EMAIL")
        val email: String?,

        @Column(name = "INFOKARTA_ID_BLOBBOX")
        val factSheetBlobBoxId: String?,

        @Column(name = "INFOKARTA_UWAGI")
        val factSheetComments: String?,

        @Column(name = "GUID")
        val guid: String,

        @Column(name = "UID_POLON")
        val polonUid: String?,

        @JsonIgnore
        @OneToMany(mappedBy = "organizationalUnit", fetch = FetchType.LAZY)
        val persons: Set<Person>,

        @JsonIgnore
        @OneToMany(mappedBy = "socialBenefitsSource", fetch = FetchType.LAZY)
        val socialBenefitsSourcePersons: Set<Person>
        )