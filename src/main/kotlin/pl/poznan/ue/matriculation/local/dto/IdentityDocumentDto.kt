package pl.poznan.ue.matriculation.local.dto

import java.io.Serializable
import java.time.LocalDate

/**
 * A DTO for the {@link pl.poznan.ue.matriculation.local.domain.applicants.IdentityDocument} entity
 */
data class IdentityDocumentDto(
    val id: Long? = null,
    val country: String? = null,
    val expDate: LocalDate? = null,
    val number: String? = null,
    val type: String? = null
) : Serializable
