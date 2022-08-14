package pl.poznan.ue.matriculation.local.dto

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class SelectionListValueListSerializer : JsonSerializer<() -> List<SelectionListValue>>() {
    override fun serialize(
        p0: (() -> List<SelectionListValue>),
        jsonGenerator: JsonGenerator,
        p2: SerializerProvider
    ) {
        val selectionListValues = p0.invoke()
        jsonGenerator.writeStartArray()
        selectionListValues.forEach {
            jsonGenerator.writeObject(it)
        }
        jsonGenerator.writeEndArray()
    }
}
