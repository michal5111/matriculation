package pl.ue.poznan.matriculation.oracle.domain

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "DZ_TYPY_TELEFONOW")
data class PhoneNumberType(
    @Id
    @NotBlank
    @Column(name = "KOD", length = 20, nullable = false)
    val code: String,

    @Column(name = "OPIS", length = 100, nullable = false)
    var description: String,

    @Column(name = "CZY_DOMYSLNY", length = 1, nullable = false)
    var isDefault: Char,

//    @Column(name = "UTW_ID", nullable = false)
//    val creatorOracleUser: String? = null,
//
//    @Column(name = "UTW_DATA", nullable = false)
//    val creationDate: Date? = null,
//
//    @Column(name = "MOD_ID", nullable = false)
//    val modificationOracleUser: String? = null,
//
//    @Column(name = "MOD_DATA", nullable = false)
//    val modificationDate: Date? = null,

    @OneToMany(mappedBy = "phoneNumberType")
    var phoneNumbers: List<PhoneNumber>
)