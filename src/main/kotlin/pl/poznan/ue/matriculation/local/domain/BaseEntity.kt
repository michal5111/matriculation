package pl.poznan.ue.matriculation.local.domain

import javax.persistence.MappedSuperclass
import javax.persistence.Version

@MappedSuperclass
open class BaseEntity {

    @Version
    val version: Long = 1
}