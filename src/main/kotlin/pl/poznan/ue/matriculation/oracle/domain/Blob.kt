package pl.poznan.ue.matriculation.oracle.domain

import javax.persistence.*

@Entity
@Table(name = "DZ_BLOBY")
class Blob(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DZ_BLOB_SEQ")
    @SequenceGenerator(sequenceName = "DZ_BLOB_SEQ", allocationSize = 1, name = "DZ_BLOB_SEQ")
    @Column(name = "ID", length = 10)
    val id: Long? = null,

    @Basic(fetch = FetchType.LAZY)
    @Lob
    @Column(name = "OBIEKT", nullable = false)
    val blob: java.sql.Blob,

    @Column(name = "KATEGORIA", length = 100, nullable = true)
    val category: String?,

    @Column(name = "OPIS", length = 1000, nullable = false)
    val description: String?,

    @OneToMany(mappedBy = "pictureBlob", fetch = FetchType.LAZY)
    val organizationalUnitPictures: List<OrganizationalUnit>,

    @OneToMany(mappedBy = "logoBlob", fetch = FetchType.LAZY)
    val organizationalUnitLogos: List<OrganizationalUnit>,

    @OneToMany(mappedBy = "logoBlob", fetch = FetchType.LAZY)
    val schoolLogo: List<School>
) : BaseEntity() {
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
