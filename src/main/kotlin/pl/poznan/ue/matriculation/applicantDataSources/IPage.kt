package pl.poznan.ue.matriculation.applicantDataSources

interface IPage<T> {
    fun getSize(): Int

    fun getResultsList(): List<T>

    fun hasNext(): Boolean
}