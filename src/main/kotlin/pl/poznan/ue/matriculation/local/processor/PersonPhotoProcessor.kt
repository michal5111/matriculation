package pl.poznan.ue.matriculation.local.processor

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.poznan.ue.matriculation.kotlinExtensions.toSerialBlob
import pl.poznan.ue.matriculation.local.dto.ProcessResult
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.domain.PersonPhoto
import pl.poznan.ue.matriculation.oracle.domain.PersonPreference

open class PersonPhotoProcessor(
    targetSystemProcessor: TargetSystemProcessor<Person>
) : ProcessDecorator<Person>(targetSystemProcessor) {

    private val logger: Logger = LoggerFactory.getLogger(PersonPhotoProcessor::class.java)

    @Transactional(
        rollbackFor = [java.lang.Exception::class, RuntimeException::class],
        propagation = Propagation.REQUIRED,
        transactionManager = "oracleTransactionManager"
    )
    override fun process(processRequest: ProcessRequest): ProcessResult<Person> {
        return super.process(processRequest).also { processResult ->
            val person = processResult.person
            val applicant = processRequest.application.applicant ?: return@also
            logger.trace("Czekam na zdjęcie")
            applicant.photoByteArrayFuture?.get()?.let { photoByteArray ->
                logger.trace("Pobrałem zdjęcie. Sprawdzam czy osoba ma zdjęcie.")
                if (person.personPhoto != null) {
                    logger.trace("Osoba ma zdjęcie. Aktualizuję...")
                    person.personPhoto?.photoBlob = photoByteArray.toSerialBlob()
                } else {
                    logger.trace("Osoba nie ma zdjęcia. Tworzę...")
                    person.personPhoto = PersonPhoto(
                        person = person,
                        photoBlob = photoByteArray.toSerialBlob()
                    )
                }
                logger.trace("Zakończyłem przetwarzanie zdjęcia.")
                val personPreference = person.personPreferences.find {
                    it.attribute == "photo_visibility"
                }
                if (personPreference != null) {
                    personPreference.value = applicant.photoPermission
                } else {
                    person.addPersonPreference(
                        PersonPreference(person, "photo_visibility", applicant.photoPermission)
                    )
                }
            }
        }
    }
}
