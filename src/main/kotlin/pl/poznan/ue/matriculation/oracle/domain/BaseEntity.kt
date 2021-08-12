package pl.poznan.ue.matriculation.oracle.domain

import org.hibernate.annotations.Type
import org.joda.time.DateTime
import javax.persistence.Column
import javax.persistence.MappedSuperclass
import javax.persistence.Version

@MappedSuperclass
open class BaseEntity {
//    @Column(name = "UTW_ID", nullable = false, columnDefinition = "VARCHAR2(30 CHAR) DEFAULT user NOT NULL ENABLE")
//    val creatorOracleUser: String? = null
//
//    @Column(name = "UTW_DATA", nullable = false, columnDefinition = "DATE DEFAULT sysdate NOT NULL ENABLE")
//    val creationDate: Timestamp = Timestamp.from(Instant.now())
//
//    @Column(name = "MOD_ID", nullable = false, columnDefinition = "VARCHAR2(30 CHAR) DEFAULT user NOT NULL ENABLE")
//    val modificationOracleUser: String? = null

    @Version
    //@Temporal(TemporalType.TIMESTAMP)
    //@Source(SourceType.DB)
    @Type(type = "pl.poznan.ue.matriculation.oracle.customHibernateTypes.OracleDateType")
    @Column(name = "MOD_DATA", nullable = false, columnDefinition = "DATE DEFAULT sysdate NOT NULL ENABLE")
    var modificationDate: DateTime? = null
}