package pl.poznan.ue.matriculation.local.dto

import pl.poznan.ue.matriculation.local.domain.enum.DuplicateStatus

data class ApplicantUsosIdAndPotentialDuplicateStatusDto(
    val usosId: Long?,
    val potentialDuplicateStatus: DuplicateStatus
)