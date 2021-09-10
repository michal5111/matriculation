package pl.poznan.ue.matriculation.oracle.customHibernateTypes

import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.usertype.ParameterizedType
import org.jadira.usertype.dateandtime.joda.columnmapper.TimestampColumnDateTimeMapper
import org.jadira.usertype.spi.shared.AbstractVersionableUserType
import org.jadira.usertype.spi.shared.IntegratorConfiguredType
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Timestamp
import java.util.*

class OracleDateType : AbstractVersionableUserType<DateTime, Timestamp, TimestampColumnDateTimeMapper>(),
    ParameterizedType,
    IntegratorConfiguredType {

    val logger: Logger = LoggerFactory.getLogger(OracleDateType::class.java)

    override fun compare(date1: Any?, date2: Any?): Int {
        return (date1 as DateTime).compareTo(date2 as DateTime)
    }

    override fun next(current: Any?, session: SharedSessionContractImplementor): DateTime? {
        val connection = session.connection()
        var ps: PreparedStatement? = null
        var result: DateTime? = null
        try {
            ps = connection.prepareStatement("select sysdate as system_date from dual")
            val rs = ps.executeQuery()
            if (rs.next()) {
                val calendar = Calendar.getInstance()
                calendar.timeZone = TimeZone.getTimeZone("UTC")
                val sysdate = rs.getTimestamp("system_date", calendar)
                logger.debug("Generated Timestamp: {}", sysdate)
                result = DateTime(sysdate)
                logger.debug("Generated DateTime: {}", result)
            }
        } catch (e: SQLException) {
            throw HibernateException("Unable to generate sysdate version")
        } finally {
            ps?.let {
                try {
                    ps.close()
                } catch (e: SQLException) {
                    throw HibernateException("Unable to close prepared statement.")
                }
            }
        }
        return result
    }
}
