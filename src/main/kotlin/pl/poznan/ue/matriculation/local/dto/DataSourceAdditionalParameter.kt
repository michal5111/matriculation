package pl.poznan.ue.matriculation.local.dto

interface DataSourceAdditionalParameter {
    val type: ParameterType
    val name: String
    val value: Any?
    val placeholderValue: String?
}
