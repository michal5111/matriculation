package pl.poznan.ue.matriculation.local.dto

import java.io.Serializable

interface IApplicantDto : Serializable {
    val foreignId: Long
}