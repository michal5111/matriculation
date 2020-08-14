package pl.poznan.ue.matriculation.dreamApply.dto.application

enum class EducationLevelType(val levelName: String, val usosCode: Char?, val programmeLevel: String) {
    SE("Wykształcenie średnie", null, "S1"),
    BA("Licencjat", 'L', "S2"),
    MA("Dyplom magisterski", 'M', "S2"),
    PD("Stopień doktorski", 'R', "S3")
}
