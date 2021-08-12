package pl.poznan.ue.matriculation.kotlinExtensions

import org.springframework.data.jpa.repository.JpaRepository
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*


fun Exception.stackTraceToString(): String {
    val sw = StringWriter()
    this.printStackTrace(PrintWriter(sw))
    return sw.toString()
}

fun String.nameCapitalize(): String {
    return this.lowercase(Locale.getDefault()).split(" ").joinToString(" ") { s ->
        s.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}

fun <T, ID> JpaRepository<T, ID>.getById(it: ID): T {
    return this.getOne(it!!)
}