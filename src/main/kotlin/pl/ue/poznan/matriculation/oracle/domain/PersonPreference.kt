package pl.ue.poznan.matriculation.oracle.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "DZ_PREFERENCJE_OSOB")
data class PersonPreference(
        @Id
        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "OS_ID", referencedColumnName = "ID", nullable = false, unique = false)
        var person: Person,

        @Id
        @Column(name = "ATRYBUT", length = 40, nullable = false)
        var attribute: String,

        @Column(name = "WARTOSC", length = 40, nullable = true)
        var value: String? = null

): Serializable