package pl.ue.poznan.matriculation.configuration

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactory",
        basePackages = ["pl.ue.poznan.matriculation.local.repo"]
)
class LocalDbConfig {

    @Value("\${spring.datasource.url}")
    private lateinit var localDbUrl: String

    @Value("\${spring.datasource.username}")
    private lateinit var localDbUsername: String

    @Value("\${spring.datasource.password}")
    private lateinit var localDbPassword: String

    @Value("\${spring.datasource.driverClassName}")
    private lateinit var localDbDriverClassName: String

    @Primary
    @Bean(name = ["dataSource"])
    @ConfigurationProperties(prefix = "spring.datasource")
    fun dataSource(): DataSource? {
        return DataSourceBuilder.create()
                .url(localDbUrl)
                .username(localDbUsername)
                .password(localDbPassword)
                .driverClassName(localDbDriverClassName)
                .build()
    }

    @Primary
    @Bean(name = ["entityManagerFactory"])
    fun entityManagerFactory(
            builder: EntityManagerFactoryBuilder,
            @Qualifier("dataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean? {
        return builder
                .dataSource(dataSource)
                .packages("pl.ue.poznan.matriculation.local.domain")
                .persistenceUnit("local")
                .build()
    }

    @Primary
    @Bean(name = ["transactionManager"])
    fun transactionManager(
            @Qualifier("entityManagerFactory") entityManagerFactory: EntityManagerFactory
    ): JpaTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}