package pl.poznan.ue.matriculation.configuration

import org.hibernate.cfg.AvailableSettings
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
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
import javax.annotation.Resource
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "entityManagerFactory",
    basePackages = ["pl.poznan.ue.matriculation.local.repo"],
    transactionManagerRef = "transactionManager"
)
class LocalDbConfig {

    @Value("\${local.datasource.url}")
    private lateinit var localDbUrl: String

    @Value("\${local.datasource.username}")
    private lateinit var localDbUsername: String

    @Value("\${local.datasource.password}")
    private lateinit var localDbPassword: String

    @Value("\${local.datasource.driverClassName}")
    private lateinit var localDbDriverClassName: String

    @Value("\${local.datasource.database-platform}")
    private lateinit var localDbHibernateDialect: String

    @Value("\${local.datasource.ddl-auto}")
    private lateinit var localDbHibernateDdlAuto: String

    @Resource
    lateinit var context: AbstractApplicationContext

    @Primary
    @Bean(name = ["dataSource"])
    @ConfigurationProperties(prefix = "local.datasource")
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
    ): LocalContainerEntityManagerFactoryBean {
        val properties: MutableMap<String, Any> = HashMap()
        properties["hibernate.hbm2ddl.auto"] = localDbHibernateDdlAuto
        properties["hibernate.dialect"] = localDbHibernateDialect
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
