package pl.poznan.ue.matriculation.oracle.customHibernateTypes

import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.usertype.ParameterizedType
import org.jadira.usertype.dateandtime.joda.columnmapper.TimestampColumnDateTimeMapper
import org.jadira.usertype.spi.shared.AbstractVersionableUserType
import org.jadira.usertype.spi.shared.IntegratorConfiguredType
import org.joda.time.DateTime
import java.sql.Timestamp

class OracleDateType : AbstractVersionableUserType<DateTime, Timestamp, TimestampColumnDateTimeMapper>(),
    ParameterizedType,
    IntegratorConfiguredType {
    override fun compare(date1: Any?, date2: Any?): Int {
        return (date1 as DateTime).secondOfDay().roundHalfEvenCopy()
            .compareTo((date2 as DateTime).secondOfDay().roundHalfEvenCopy())
    }

    override fun next(current: Any?, session: SharedSessionContractImplementor?): DateTime? {
        return super.next(current, session).secondOfDay().roundHalfEvenCopy()
    }
}