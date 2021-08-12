package pl.poznan.ue.matriculation.oracle.repo

interface DidacticCycleRepositoryCustom {
    fun findDidacticCycleCodes(didacticCycleCode: String, maxResults: Int): List<String>
}