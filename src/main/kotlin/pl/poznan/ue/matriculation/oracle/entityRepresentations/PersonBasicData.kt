package pl.poznan.ue.matriculation.oracle.entityRepresentations

import java.util.*

interface PersonBasicData {
    val id: Long
    val name: String
    val middleName: String?
    val surname: String
    val idNumber: String?
    val birthDate: Date
    val privateEmail: String?
    val email: String?
    val sex: Char
}