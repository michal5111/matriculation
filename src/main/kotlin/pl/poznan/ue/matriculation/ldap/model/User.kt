package pl.poznan.ue.matriculation.ldap.model

import org.springframework.ldap.odm.annotations.Attribute
import org.springframework.ldap.odm.annotations.Entry
import org.springframework.ldap.odm.annotations.Id
import javax.naming.Name

@Entry(
    base = "DC=ue,DC=poznan",
    objectClasses = ["organizationalPerson", "person", "top", "user"]
)
data class User(
    @Id
    val name: Name? = null,

    @Attribute(name = "cn") val uid: String? = null,

    @Attribute(name = "extensionAttribute14") val usosId: Long? = null,

    @Attribute(name = "mail") val email: String? = null,

    @Attribute(name = "givenName") val givenName: String? = null,

    @Attribute(name = "sn") val surname: String? = null
)
