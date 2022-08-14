package pl.poznan.ue.matriculation.local.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class SelectionListDataSourceAdditionalParameter(
    override val name: String,
    override val value: Any?,
    @JsonSerialize(using = SelectionListValueListSerializer::class)
    val selectionListSource: () -> List<SelectionListValue>
) : DataSourceAdditionalParameter {
    override val type: ParameterType
        get() = ParameterType.SELECTION_LIST
    override val placeholderValue: String? = null
}
