package pl.poznan.ue.matriculation.cem.enum

enum class ApplicationStatus {
    UNKNOWN,
    NEW, // Nowe zgłoszenie
    PENDING, //Kandydat na liście przyjętych (bez dokumentów)
    WAITING, //Kandydat na liście rezerwowej
    CONFIRMED, //Kandydat ze złożonymi dokumentami
    ACCEPTED, //Słuchacz
    FINISHED, //Absolwent
    REMOVED, //Skreślony
    CANCELED, //Aplikacja anulowana
    EDITION_CLOSED, //Studia nieuruchomione
}
