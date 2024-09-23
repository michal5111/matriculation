package pl.poznan.ue.matriculation.oracle.customKeyGenerator

import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.hibernate.service.ServiceRegistry
import org.hibernate.type.Type
import java.io.Serializable
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.*

class SequenceStringKeyGenerator : IdentifierGenerator {

    private lateinit var sequenceId: String

    override fun generate(session: SharedSessionContractImplementor, collection: Any): Serializable {
        val connection = session.jdbcConnectionAccess.obtainConnection()
        var ps: PreparedStatement? = null
        var result = ""
        try {
            ps = connection.prepareStatement("select ${sequenceId}.nextval as ID from dual")
            val rs = ps.executeQuery()

            if (rs.next()) {
                val pk = rs.getLong("ID")
                result = pk.toString()
            }
        } catch (e: SQLException) {
            throw HibernateException("Unable to generate Primary Key")
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

    override fun configure(type: Type, params: Properties, serviceRegistry: ServiceRegistry) {
        sequenceId = params.getProperty("sequenceId")
    }
}
