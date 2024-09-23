package pl.poznan.ue.matriculation.oracle.jpaConverters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class TAndNToBooleanConverter : AttributeConverter<Boolean?, Char?> {
    override fun convertToDatabaseColumn(aBoolean: Boolean?): Char? {
        if (aBoolean == null) return null
        return if (aBoolean) 'T' else 'N'
    }

    override fun convertToEntityAttribute(character: Char?): Boolean? {
        if (character == null) return null
        return character == 'T'
    }
}
