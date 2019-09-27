package pl.ue.poznan.matriculation.local.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Task(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,
        var status: String,
        var percentage: Int
)