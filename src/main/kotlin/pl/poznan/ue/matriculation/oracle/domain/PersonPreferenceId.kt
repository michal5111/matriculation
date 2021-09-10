package pl.poznan.ue.matriculation.oracle.domain

import java.io.Serializable

data class PersonPreferenceId(
    var person: Long? = null,

    var attribute: String? = null
) : Serializable

