package pl.poznan.ue.matriculation.applicantDataSources

interface IPage<T> {
    fun getTotalSize(): Int

    fun getContent(): Collection<T>

    fun hasNext(): Boolean
}
