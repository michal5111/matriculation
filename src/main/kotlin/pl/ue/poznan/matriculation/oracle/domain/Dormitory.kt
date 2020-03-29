package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "DZ_AKADEMIKI")
data class Dormitory(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_AKADEMIKI_SEQ")
        @SequenceGenerator(sequenceName = "DZ_AKADEMIKI_SEQ", allocationSize = 1, name = "DZ_AKADEMIKI_SEQ")
        @Column(name = "ID", length = 10)
        val id: Long? = null,

        @Column(name = "NAZWA", length = 40, nullable = false)
        var name: String,

        @Column(name = "OPIS", length = 2000, nullable = true)
        var description: String? = null,

//        @Column(name = "UTW_ID", nullable = false)
//        val creatorOracleUser: String? = null,
//
//        @Column(name = "UTW_DATA", nullable = false)
//        val creationDate: Date? = null,
//
//        @Column(name = "MOD_ID", nullable = false)
//        val modificationOracleUser: String? = null,
//
//        @Column(name = "MOD_DATA", nullable = false)
//        val modificationDate: Date? = null,

        @Column(name = "SKROT", length = 5, nullable = true)
        var short: String? = null,

        @Column(name = "OPIS_ANG", length = 2000, nullable = true)
        var englishDescription: String? = null,

        @OneToOne(mappedBy = "dormitory", fetch = FetchType.LAZY)
        var address: Address,

        @JsonIgnore
        @OneToMany(mappedBy = "dormitory")
        var phoneNumbers: List<PhoneNumber>
)