package pl.poznan.ue.matriculation.local.dto

import java.io.Serializable
import java.util.*

/**
 * A DTO for the {@link pl.poznan.ue.matriculation.local.domain.applicants.IdentityDocument} entity
 */
data class IdentityDocumentDto(
    val id: Long? = null,
    val country: String? = null,
    val expDate: Date? = null,
    val number: String? = null,
    val type: String? = null
) : Serializable
