package pl.poznan.ue.matriculation.oracle.entityRepresentations

import java.time.LocalDate

interface PersonBasicData {
    val id: Long
    val name: String
    val middleName: String?
    val surname: String
    val idNumber: String?
    val pesel: String?
    val birthDate: LocalDate
    val privateEmail: String?
    val email: String?
    val sex: Char
}
