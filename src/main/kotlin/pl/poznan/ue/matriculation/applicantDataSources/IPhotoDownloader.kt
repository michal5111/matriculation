package pl.poznan.ue.matriculation.applicantDataSources

interface IPhotoDownloader {
    fun getPhoto(photoUrl: String): ByteArray?
}