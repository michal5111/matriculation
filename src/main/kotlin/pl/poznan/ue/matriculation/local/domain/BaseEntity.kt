package pl.poznan.ue.matriculation.local.domain

import java.io.Serializable
import javax.persistence.MappedSuperclass
import javax.persistence.Version

@MappedSuperclass
open class BaseEntity : Serializable {

    @Version
    open var version: Long? = 1
}
