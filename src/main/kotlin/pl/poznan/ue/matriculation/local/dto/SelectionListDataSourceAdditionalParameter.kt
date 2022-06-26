package pl.poznan.ue.matriculation.local.dto

class SelectionListDataSourceAdditionalParameter(
    override val name: String,
    override val value: Any?,
    selectionListSource: () -> List<SelectionListValue>
) : DataSourceAdditionalParameter {
    override val type: ParameterType
        get() = ParameterType.SELECTION_LIST
    override val placeholderValue: String? = null
}
