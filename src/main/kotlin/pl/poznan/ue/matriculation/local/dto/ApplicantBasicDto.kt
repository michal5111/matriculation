package pl.poznan.ue.matriculation.local.dto

import java.time.ZonedDateTime

data class ApplicantBasicDto(
    val foreignId: Long,
    val dataSourceId: String?,
    val email: String,
    val indexNumber: String?,
    val citizenship: String?,
    val nationality: String?,
    val photo: String?,
    val photoPermission: String?,
    val modificationDate: ZonedDateTime,
    val usosId: Long?,
)
