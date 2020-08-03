package pl.poznan.ue.matriculation.dreamApply.dto.application

enum class EducationLevelType(val levelName: String, val usosCode: Char?) {
    SE("Wykształcenie średnie", null),
    BA("Licencjat", 'L'),
    MA("Dyplom magisterski", 'M'),
    PD("Stopień doktorski", 'R')
}
