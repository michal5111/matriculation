package pl.poznan.ue.matriculation.local.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.time.LocalDateTime


@MappedSuperclass
open class BaseEntity : Serializable {

    @Version
    open var version: Long? = 1

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private val createDate: LocalDateTime? = null

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private val modifyDate: LocalDateTime? = null
}
