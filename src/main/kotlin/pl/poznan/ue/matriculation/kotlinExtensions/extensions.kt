package pl.poznan.ue.matriculation.kotlinExtensions

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