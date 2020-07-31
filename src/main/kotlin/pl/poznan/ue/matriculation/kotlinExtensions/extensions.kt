package pl.poznan.ue.matriculation.kotlinExtensions

import java.io.PrintWriter
import java.io.StringWriter


fun Exception.stackTraceToString(): String {
    val sw = StringWriter()
    this.printStackTrace(PrintWriter(sw))
    return sw.toString()
}

fun String.nameCapitalize(): String {
    return this.toLowerCase().split(" ").joinToString(" ") {
        it.capitalize()
    }
}