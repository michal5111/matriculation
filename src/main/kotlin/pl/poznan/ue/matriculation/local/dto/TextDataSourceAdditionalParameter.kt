package pl.poznan.ue.matriculation.local.dto

data class TextDataSourceAdditionalParameter(
    override val name: String,
    override val placeholderValue: String?
) : DataSourceAdditionalParameter {
    override val type: ParameterType = ParameterType.TEXT
    override val value: String? = null
}
