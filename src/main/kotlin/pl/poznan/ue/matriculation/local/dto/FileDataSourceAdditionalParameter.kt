package pl.poznan.ue.matriculation.local.dto

data class FileDataSourceAdditionalParameter(
    override val name: String,
    val accept: String,
    val multiple: Boolean,
    val fileTemplate: String?,
    override val placeholderValue: String?
) : DataSourceAdditionalParameter {
    override val type: ParameterType = ParameterType.FILE
    override val value: String? = null
}
