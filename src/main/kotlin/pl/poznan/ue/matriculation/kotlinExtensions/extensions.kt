package pl.poznan.ue.matriculation.kotlinExtensions

import org.apache.poi.ss.formula.functions.T
import pl.poznan.ue.matriculation.local.domain.user.User
import pl.poznan.ue.matriculation.local.dto.RoleDto
import pl.poznan.ue.matriculation.local.dto.UserDto
import pl.poznan.ue.matriculation.local.service.AsyncService
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob
import javax.sql.rowset.serial.SerialClob

fun Exception.stackTraceToHtmlString(): String = this.stackTraceToString()
    .replace("\n", "<br>")
    .replace("\t", "&emsp;")

//fun String.nameCapitalize(): String {
//    return this.trim().lowercase(Locale.getDefault()).split(" -").joinToString(" ") { s ->
//        s.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
//    }
//}

inline fun <T> MutableList<T>.mutate(mutator: (T) -> Unit) {
    val iterate = this.listIterator()
    while (iterate.hasNext()) {
        val item = iterate.next()
        mutator(item)
    }
}

inline fun <T> Iterable<T>.forEachAsync(asyncService: AsyncService, crossinline action: (T) -> Unit) {
    for (element in this) asyncService.doAsync {
        action(element)
    }
}

inline fun retry(
    maxRetry: Int = 1,
    retryOn: Array<Class<out Throwable>> = arrayOf(Exception::class.java),
    action: (retryCount: Int) -> Unit
) {
    var retryCount = 0
    while (true) {
        try {
            action(retryCount)
            break
        } catch (e: Exception) {
            if (retryOn.none { e::class == it || it.isAssignableFrom(e::class.java) } || ++retryCount == maxRetry) throw e
        }
    }
}

fun ByteArray.toSerialBlob(): SerialBlob {
    return SerialBlob(this)
}

fun CharArray.toSerialClob(): SerialClob {
    return SerialClob(this)
}

fun String.toSerialClob(): SerialClob {
    return SerialClob(this.toCharArray())
}

fun Blob?.toByteArray(): ByteArray? {
    return this?.getBytes(1, length().toInt())
}

fun String.trimPostalCode() = this.replace("[^0-9]".toRegex(), "").ifBlank { null }

fun String.trimPhoneNumber() = this.replace("[^0-9+]".toRegex(), "").ifBlank { null }

data class ParsedAddress(val street: String?, val streetNumber: String?, val flatNumber: String?)

fun parseAddressFromString(address: String?): ParsedAddress {
    val addressStreetAndFlatNumber = address
        ?.substringAfterLast(' ', "")
        ?.takeIf { it.matches(".*\\d.*".toRegex()) }?.split('/')
    val streetNumber = addressStreetAndFlatNumber?.getOrNull(0)
    val flatNumber = addressStreetAndFlatNumber?.getOrNull(1)
    var street = address?.takeIf { it.isNotBlank() }
    if (streetNumber != null) {
        street = street?.substringBeforeLast(' ')?.takeIf { it.isNotBlank() }
    }
    return ParsedAddress(street = street, streetNumber = streetNumber, flatNumber = flatNumber)
}

fun User.toUserDto() = let {
    UserDto(
        id = it.id,
        uid = it.uid,
        roles = it.roles.map { role ->
            RoleDto(
                code = role.code,
                name = role.name
            )
        },
        givenName = it.givenName,
        surname = it.surname,
        email = it.email,
        usosId = it.usosId,
        version = it.version
    )
}
