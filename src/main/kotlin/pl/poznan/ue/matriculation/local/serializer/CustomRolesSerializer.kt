package pl.poznan.ue.matriculation.local.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import pl.poznan.ue.matriculation.local.domain.user.Role
import pl.poznan.ue.matriculation.local.domain.user.UserRole
import java.util.*

class CustomRolesSerializer(t: Class<MutableSet<UserRole>>?) : StdSerializer<MutableSet<UserRole>>(t) {

    override fun serialize(userRolesSet: MutableSet<UserRole>?, jg: JsonGenerator, sp: SerializerProvider) {
        val rolesSet: MutableList<Role> = LinkedList()
        userRolesSet?.map {
            it.role
        }?.let {
            rolesSet.addAll(it)
        }
        jg.writeObject(rolesSet)
    }
}
