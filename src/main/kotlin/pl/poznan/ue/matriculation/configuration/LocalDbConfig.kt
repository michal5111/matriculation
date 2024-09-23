package pl.poznan.ue.matriculation.configuration

import jakarta.annotation.Resource
import jakarta.persistence.EntityManagerFactory
import org.hibernate.cfg.AvailableSettings
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.support.AbstractApplicationContext
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.hibernate5.SpringBeanContainer
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.annotation.EnableTransactionManagement
import pl.poznan.ue.matriculation.properties.LocalDBProperties
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "entityManagerFactory",
    basePackages = ["pl.poznan.ue.matriculation.local.repo"],
    transactionManagerRef = "transactionManager"
)
class LocalDbConfig(
    private val localDBProperties: LocalDBProperties
) {

    @Resource
    lateinit var context: AbstractApplicationContext

    @Primary
    @Bean(name = ["dataSource"])
    fun dataSource(): DataSource? {
        return DataSourceBuilder.create()
            .url(localDBProperties.url)
            .username(localDBProperties.username)
            .password(localDBProperties.password)
            .driverClassName(localDBProperties.driverClassName)
            .build()
    }

    @Primary
    @Bean(name = ["entityManagerFactory"])
    fun entityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("dataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        val properties: Map<String, Any> = localDBProperties.jpa ?: HashMap()
        val em = builder
            .dataSource(dataSource)
            .packages("pl.poznan.ue.matriculation.local.domain")
            .persistenceUnit("local")
            .properties(properties)
            .build()
        em.jpaPropertyMap[AvailableSettings.BEAN_CONTAINER] = SpringBeanContainer(context.beanFactory)
        return em
    }

    @Primary
    @Bean(name = ["transactionManager"])
    fun transactionManager(
        @Qualifier("entityManagerFactory") entityManagerFactory: EntityManagerFactory
    ): JpaTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}
