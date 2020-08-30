package pl.poznan.ue.matriculation.dreamApply.dto.application

enum class EducationLevelType(val levelName: String, val usosCode: Char?, val programmeLevel: String) {
    SE("Wykształcenie średnie", null, "1"),
    BA("Licencjat", 'L', "2"),
    MA("Dyplom magisterski", 'M', "2"),
    PD("Stopień doktorski", 'R', "3")
}
