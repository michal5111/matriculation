package pl.poznan.ue.matriculation

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer


@SpringBootApplication
@ConfigurationPropertiesScan
class MatriculationApplication : SpringBootServletInitializer()

fun main(args: Array<String>) {
    runApplication<MatriculationApplication>(*args)
}
