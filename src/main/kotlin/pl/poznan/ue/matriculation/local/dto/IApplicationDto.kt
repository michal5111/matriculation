package pl.poznan.ue.matriculation.local.dto

import java.io.Serializable

interface IApplicationDto : Serializable {
    val foreignApplicantId: Long
    val foreignId: Long
}