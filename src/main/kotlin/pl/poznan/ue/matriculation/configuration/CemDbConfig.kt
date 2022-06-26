package pl.poznan.ue.matriculation.configuration

import org.hibernate.cfg.AvailableSettings
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.AbstractApplicationContext
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.orm.hibernate5.SpringBeanContainer
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.annotation.EnableTransactionManagement
import pl.poznan.ue.matriculation.properties.CemDatasourceProperties
import javax.annotation.Resource
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "cemEntityManagerFactory",
    transactionManagerRef = "cemTransactionManager",
    basePackages = ["pl.poznan.ue.matriculation.cem.repo"]
)
@ConditionalOnProperty(
    value = ["pl.poznan.ue.matriculation.cem.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class CemDbConfig(
    cemDatasourceProperties: CemDatasourceProperties
) {
    private val cemDBProperties = cemDatasourceProperties.db

    @Resource
    lateinit var context: AbstractApplicationContext

    @Bean(name = ["cemDataSource"])
    fun dataSource(): DataSource? {
        return DataSourceBuilder.create()
            .url(cemDBProperties.url)
            .username(cemDBProperties.username)
            .password(cemDBProperties.password)
            .driverClassName(cemDBProperties.driverClassName)
            .build()
    }

    @Bean(name = ["cemEntityManagerFactory"])
    fun cemEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("cemDataSource") dataSource: DataSource
    ):
        LocalContainerEntityManagerFactoryBean {
        val properties: Map<String, Any> = cemDBProperties.jpa ?: HashMap()
        val em = builder
            .dataSource(dataSource)
            .packages("pl.poznan.ue.matriculation.cem.domain")
            .persistenceUnit("cem")
            .properties(properties)
            .build()
        em.jpaPropertyMap[AvailableSettings.BEAN_CONTAINER] = SpringBeanContainer(context.beanFactory)
        return em
    }

    @Bean(name = ["cemTransactionManager"])
    fun cemTransactionManager(@Qualifier("cemEntityManagerFactory") cemEntityManagerFactory: EntityManagerFactory):
        JpaTransactionManager {
        return JpaTransactionManager(cemEntityManagerFactory)
    }

    @Bean(name = ["cemJdbcTemplate"])
    fun cemJdbcTemplate(@Qualifier("cemDataSource") dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }
}
