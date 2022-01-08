package pl.poznan.ue.matriculation.configuration

import org.hibernate.cfg.AvailableSettings
import org.springframework.beans.factory.annotation.Qualifier
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
import pl.poznan.ue.matriculation.properties.OracleDBProperties
import javax.annotation.Resource
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "oracleEntityManagerFactory",
    transactionManagerRef = "oracleTransactionManager",
    basePackages = ["pl.poznan.ue.matriculation.oracle.repo"]
)
class OracleDbConfig(
    private val oracleDBProperties: OracleDBProperties
) {
    @Resource
    lateinit var context: AbstractApplicationContext

    @Bean(name = ["oracleDataSource"])
    fun dataSource(): DataSource? {
        return DataSourceBuilder.create()
            .url(oracleDBProperties.url)
            .username(oracleDBProperties.username)
            .password(oracleDBProperties.password)
            .driverClassName(oracleDBProperties.driverClassName)
            .build()
    }

    @Bean(name = ["oracleEntityManagerFactory"])
    fun oracleEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("oracleDataSource") dataSource: DataSource
    ):
        LocalContainerEntityManagerFactoryBean {
        val properties: MutableMap<String, Any> = HashMap()
        properties["hibernate.dialect"] = oracleDBProperties.databasePlatform
        val em = builder
            .dataSource(dataSource)
            .packages("pl.poznan.ue.matriculation.oracle.domain")
            .persistenceUnit("oracle")
            .properties(properties)
            .build()
        em.jpaPropertyMap[AvailableSettings.BEAN_CONTAINER] = SpringBeanContainer(context.beanFactory)
        return em
    }

    @Bean(name = ["oracleTransactionManager"])
    fun oracleTransactionManager(@Qualifier("oracleEntityManagerFactory") oracleEntityManagerFactory: EntityManagerFactory):
        JpaTransactionManager {
        return JpaTransactionManager(oracleEntityManagerFactory)
    }

    @Bean(name = ["oracleJdbcTemplate"])
    fun oracleJdbcTemplate(@Qualifier("oracleDataSource") dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }

//    @Bean(name = ["chainedTransactionManager"])
//    fun transactionManager(@Qualifier("oracleDataSource") ds1: PlatformTransactionManager?,
//                           @Qualifier("secondaryDs") ds2: PlatformTransactionManager?): ChainedTransactionManager? {
//        return ChainedTransactionManager(ds1, ds2)
//    }
}
