package pl.poznan.ue.matriculation.irk.dto.registrations

enum class RegistrationStatus(val status: String) {
    NEW("new"),
    PUBLISHED("published"),
    HALTED("halted"),
    ENDED("ended"),
    TO_BE_ARCHIVED("to_be_archived"),
    ARCHIVED("archived")
}
