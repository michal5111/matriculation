package pl.poznan.ue.matriculation.local.dto

import pl.poznan.ue.matriculation.local.domain.enum.DuplicateStatus
import java.io.Serializable
import java.util.*

data class ApplicantDto(
    val id: Long? = null,

    val foreignId: Long? = null,

    val dataSourceId: String? = null,

    val email: String? = null,

    val indexNumber: String? = null,

    val password: String? = null,

    val citizenship: String? = null,

    val nationality: String? = null,

    val photo: String? = null,

    val photoPermission: String? = null,

    val modificationDate: Date? = null,

    val usosId: Long? = null,

    val assignedIndexNumber: String? = null,

    val potentialDuplicateStatus: DuplicateStatus = DuplicateStatus.NOT_CHECKED,

    val uid: String? = null,

    val fathersName: String? = null,

    val militaryCategory: String? = null,

    val militaryStatus: String? = null,

    val mothersName: String? = null,

    val wku: String? = null,

    val sex: Char? = null,

    val pesel: String? = null,

    val dateOfBirth: Date? = null,

    val cityOfBirth: String? = null,

    val countryOfBirth: String? = null,

    val highSchoolCity: String? = null,

    val highSchoolName: String? = null,

    val highSchoolType: String? = null,

    val highSchoolUsosCode: Long? = null,

    val middle: String? = null,

    val family: String? = null,

    val given: String? = null,

    val maiden: String? = null,

    val personExisted: Boolean = false,

    val primaryIdentityDocument: IdentityDocumentDto? = null
) : Serializable
