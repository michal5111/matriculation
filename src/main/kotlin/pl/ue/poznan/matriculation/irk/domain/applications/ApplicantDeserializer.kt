package pl.ue.poznan.matriculation.irk.domain.applications

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pl.ue.poznan.matriculation.irk.service.IrkService
import pl.ue.poznan.matriculation.local.domain.applicants.Applicant

@Component
class ApplicantDeserializer(): JsonDeserializer<Applicant>() {

    companion object {
        @JvmStatic lateinit var irkService: IrkService
    }

    @Autowired
    constructor(irkService: IrkService) : this() {
        ApplicantDeserializer.irkService = irkService
    }

    override fun deserialize(jp: JsonParser?, dc: DeserializationContext?): Applicant {
        val id: Long = jp!!.readValueAs(Long::class.java)
        return irkService.getApplicantById(id)!!
    }
}