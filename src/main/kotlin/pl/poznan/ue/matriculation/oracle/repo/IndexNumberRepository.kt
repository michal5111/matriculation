package pl.poznan.ue.matriculation.oracle.repo

import pl.poznan.ue.matriculation.oracle.dto.IndexNumberDto

interface IndexNumberRepository {
    fun getNewIndexNumber(indexPoolCode: String): IndexNumberDto
}