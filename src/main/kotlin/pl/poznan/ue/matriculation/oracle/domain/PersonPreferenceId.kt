package pl.poznan.ue.matriculation.oracle.domain

import java.io.Serializable

data class PersonPreferenceId(
        var person: Long? = null,

        var attribute: String? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonPreferenceId

        if (person != other.person) return false
        if (attribute != other.attribute) return false

        return true
    }

    override fun hashCode(): Int {
        var result = person.hashCode()
        result = 31 * result + attribute.hashCode()
        return result
    }
}

