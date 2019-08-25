package pl.ue.poznan.matriculation

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class MatriculationApplication

fun main(args: Array<String>) {
	runApplication<MatriculationApplication>(*args)
}
