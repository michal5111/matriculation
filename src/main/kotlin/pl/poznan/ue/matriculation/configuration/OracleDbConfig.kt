package pl.poznan.ue.matriculation.configuration

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "oracleEntityManagerFactory",
        transactionManagerRef = "oracleTransactionManager",
        basePackages = ["pl.poznan.ue.matriculation.oracle.repo"]
)
class OracleDbConfig {

    @Value("\${oracle.datasource.url}")
    private lateinit var oracleDbUrl: String

    @Value("\${oracle.datasource.username}")
    private lateinit var oracleDbUsername: String

    @Value("\${oracle.datasource.password}")
    private lateinit var oracleDbPassword: String

    @Value("\${oracle.datasource.driverClassName}")
    private lateinit var oracleDbDriverClassName: String

    @Value("\${oracle.datasource.database-platform}")
    private lateinit var oracleDbHibernateDialect: String

    @Bean(name = ["oracleDataSource"])
    @ConfigurationProperties(prefix = "oracle.datasource")
    fun dataSource(): DataSource? {
        return DataSourceBuilder.create()
                .url(oracleDbUrl)
                .username(oracleDbUsername)
                .password(oracleDbPassword)
                .driverClassName(oracleDbDriverClassName)
                .build()
    }

    @Bean(name = ["oracleEntityManagerFactory"])
    fun oracleEntityManagerFactory(builder: EntityManagerFactoryBuilder,
                                   @Qualifier("oracleDataSource") dataSource: DataSource):
            LocalContainerEntityManagerFactoryBean {
        val properties: MutableMap<String, Any> = HashMap()
        properties["hibernate.dialect"] = oracleDbHibernateDialect
        return builder
                .dataSource(dataSource)
                .packages("pl.poznan.ue.matriculation.oracle.domain")
                .persistenceUnit("oracle")
                .properties(properties)
                .build()
    }

    @Bean(name = ["oracleTransactionManager"])
    fun oracleTransactionManager(@Qualifier("oracleEntityManagerFactory") oracleEntityManagerFactory: EntityManagerFactory):
            JpaTransactionManager {
        return JpaTransactionManager(oracleEntityManagerFactory)
    }

//    @Bean(name = ["chainedTransactionManager"])
//    fun transactionManager(@Qualifier("oracleDataSource") ds1: PlatformTransactionManager?,
//                           @Qualifier("secondaryDs") ds2: PlatformTransactionManager?): ChainedTransactionManager? {
//        return ChainedTransactionManager(ds1, ds2)
//    }
}