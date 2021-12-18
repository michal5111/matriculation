package pl.poznan.ue.matriculation.local.domain

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class BaseEntityLongId : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    open var id: Long? = null
}
