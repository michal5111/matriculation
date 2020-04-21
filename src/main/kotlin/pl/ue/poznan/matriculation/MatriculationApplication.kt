package pl.ue.poznan.matriculation

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer


@SpringBootApplication
class MatriculationApplication: SpringBootServletInitializer()

fun main(args: Array<String>) {
	runApplication<MatriculationApplication>(*args)
}
