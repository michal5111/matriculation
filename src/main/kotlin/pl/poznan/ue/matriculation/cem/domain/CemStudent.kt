package pl.poznan.ue.matriculation.cem.domain

import org.hibernate.annotations.Immutable
import pl.poznan.ue.matriculation.local.dto.IApplicantDto
import java.util.*
import javax.persistence.*

@Entity
@Immutable
@Table(name = "students")
class CemStudent : IApplicantDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cemStudent")
    val cemApplications: List<CemApplication> = mutableListOf()

    @Column(name = "first_name")
    var firstName: String? = null

    @Column(name = "second_name")
    var secondName: String? = null

    @Column(name = "last_name")
    var lastName: String? = null

    @Column(name = "maiden_name")
    var maidenName: String? = null

    @Column(name = "father_name")
    var fatherName: String? = null

    @Column(name = "mother_name")
    var motherName: String? = null

    @Temporal(TemporalType.DATE)
    @Column(name = "birthday")
    var birthdate: Date? = null

    @Column(name = "birth_place")
    var birthPlace: String? = null

    @Column(name = "pesel")
    var pesel: String? = null

    @Column(name = "phone")
    var phone: String? = null

    @Column(name = "mobile")
    var mobile: String? = null

    @Column(name = "email")
    var email: String? = null

    @Column(name = "sex")
    var sex: Int? = null

    @Column(name = "nationality")
    var nationality: String? = null

    @Column(name = "id_number")
    var idNumber: String? = null

    @Column(name = "id_issued_by")
    var idIssuedBy: String? = null

    @Temporal(TemporalType.DATE)
    @Column(name = "id_issue_date")
    var idIssuedDate: Date? = null

    @Column(name = "address")
    var address: String? = null

    @Column(name = "forwarding_address")
    var forwardingAddress: String? = null

    @Column(name = "address_street")
    var addressStreet: String? = null

    @Column(name = "address_postal_code_1")
    var addressPostalCode1: String? = null

    @Column(name = "address_postal_code_2")
    var addressPostalCode2: String? = null

    @Column(name = "address_city")
    var addressCity: String? = null

    @Column(name = "address2_street")
    var address2Street: String? = null

    @Column(name = "address2_postal_code_1")
    var address2PostalCode1: String? = null

    @Column(name = "address2_postal_code_2")
    var address2PostalCode2: String? = null

    @Column(name = "address2_city")
    var address2City: String? = null

    @Column(name = "address_country")
    var addressCountry: String? = null

    @Column(name = "address2_country")
    var address2Country: String? = null

    @Column(name = "is_coresp_address_given")
    var isCorrespondenceAddressGiven: Int? = null

    override val foreignId: Long
        get() = id!!
}
