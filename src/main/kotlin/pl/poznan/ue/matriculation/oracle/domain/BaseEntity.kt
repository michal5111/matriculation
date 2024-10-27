package pl.poznan.ue.matriculation.oracle.domain

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Version
import java.time.ZonedDateTime

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
    @Column(name = "MOD_DATA", nullable = false, columnDefinition = "DATE DEFAULT sysdate NOT NULL ENABLE")
    open var modificationDate: ZonedDateTime = ZonedDateTime.now()
}
