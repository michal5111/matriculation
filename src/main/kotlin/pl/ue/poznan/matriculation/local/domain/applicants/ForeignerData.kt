package pl.ue.poznan.matriculation.local.domain.applicants


import pl.ue.poznan.matriculation.irk.dto.applicants.ForeignerDataDTO
import javax.persistence.*

@Entity
data class ForeignerData(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,
        val baseOfStay: String?,
//        @ManyToOne(fetch = FetchType.LAZY)
//        @JoinColumn(referencedColumnName = "id")
//        val foreignerStatus: List<Status>,
        val polishCardIssueCountry: String?,
        val polishCardIssueDate: String?,
        val polishCardNumber: String?,
        val polishCardValidTo: String?
) {
        constructor(foreignerDataDTO: ForeignerDataDTO): this(
                baseOfStay = foreignerDataDTO.baseOfStay,
//                foreignerStatus = foreignerData.foreignerStatus.stream()
//                        .map { status -> Status(status) }
//                        .collect(Collectors.toList()),
                polishCardIssueCountry = foreignerDataDTO.polishCardIssueCountry,
                polishCardIssueDate = foreignerDataDTO.polishCardIssueDate,
                polishCardNumber = foreignerDataDTO.polishCardNumber,
                polishCardValidTo = foreignerDataDTO.polishCardValidTo
        )
}