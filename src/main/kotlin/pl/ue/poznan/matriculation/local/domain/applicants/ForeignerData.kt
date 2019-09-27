package pl.ue.poznan.matriculation.local.domain.applicants


import pl.ue.poznan.matriculation.irk.domain.applicants.ForeignerData
import java.util.stream.Collectors
import javax.persistence.*

@Entity
data class ForeignerData(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,
        val baseOfStay: String?,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(referencedColumnName = "id")
        val foreignerStatus: List<Status>,
        val polishCardIssueCountry: String?,
        val polishCardIssueDate: String?,
        val polishCardNumber: String?,
        val polishCardValidTo: String?
) {
        constructor(foreignerData: ForeignerData): this(
                baseOfStay = foreignerData.baseOfStay,
                foreignerStatus = foreignerData.foreignerStatus.stream()
                        .map { status -> Status(status) }
                        .collect(Collectors.toList()),
                polishCardIssueCountry = foreignerData.polishCardIssueCountry,
                polishCardIssueDate = foreignerData.polishCardIssueDate,
                polishCardNumber = foreignerData.polishCardNumber,
                polishCardValidTo = foreignerData.polishCardValidTo
        )
}