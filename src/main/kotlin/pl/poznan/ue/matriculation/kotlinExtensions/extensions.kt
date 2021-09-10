package pl.poznan.ue.matriculation.kotlinExtensions

import pl.poznan.ue.matriculation.local.service.AsyncService
import java.io.PrintWriter
import java.io.StringWriter
import java.sql.Blob
import java.util.*
import javax.sql.rowset.serial.SerialBlob
import javax.sql.rowset.serial.SerialClob


fun Exception.stackTraceToString(): String {
    val sw = StringWriter()
    this.printStackTrace(PrintWriter(sw))
    return sw.toString()
}

fun String.nameCapitalize(): String {
    return this.trim().lowercase(Locale.getDefault()).split(" ").joinToString(" ") { s ->
        s.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}

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
