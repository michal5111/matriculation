package pl.poznan.ue.matriculation

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.MariaDBContainer


@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [AbstractIT.Initializer::class])
abstract class AbstractIT {

    companion object {
        val mariaDBSQLContainer: MariaDBContainer<*> = MariaDBContainer("mariadb")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa")

        init {
            mariaDBSQLContainer.start()
        }
    }

    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.url=" + mariaDBSQLContainer.jdbcUrl,
                "spring.datasource.username=" + mariaDBSQLContainer.username,
                "spring.datasource.password=" + mariaDBSQLContainer.password
            ).applyTo(applicationContext.environment)
        }

    }
}
