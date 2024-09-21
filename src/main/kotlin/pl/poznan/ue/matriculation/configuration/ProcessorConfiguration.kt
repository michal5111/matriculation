package pl.poznan.ue.matriculation.configuration

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.poznan.ue.matriculation.local.processor.*
import pl.poznan.ue.matriculation.oracle.domain.Person
import pl.poznan.ue.matriculation.oracle.repo.*
import pl.poznan.ue.matriculation.oracle.service.AddressService
import pl.poznan.ue.matriculation.oracle.service.ImmatriculationService

@Configuration
class ProcessorConfiguration {

    @Bean
    @Primary
    fun immatriculationProcessor(
        immatriculationService: ImmatriculationService,
        @Qualifier("addressesProcessor") targetSystemProcessor: TargetSystemProcessor<Person>
    ): TargetSystemProcessor<Person> {
        return ImmatriculationProcessor(
            immatriculationService = immatriculationService,
            targetSystemProcessor = targetSystemProcessor
        )
    }

    @Bean
    fun addressesProcessor(
        addressService: AddressService,
        @Qualifier("entitlementDocumentProcessor") targetSystemProcessor: TargetSystemProcessor<Person>
    ): TargetSystemProcessor<Person> {
        return AddressesProcessor(addressService = addressService, targetSystemProcessor)
    }

    @Bean
    fun entitlementDocumentProcessor(
        schoolRepository: SchoolRepository,
        @Qualifier("ownedDocumentsProcessor") targetSystemProcessor: TargetSystemProcessor<Person>
    ): TargetSystemProcessor<Person> {
        return EntitlementDocumentProcessor(schoolRepository = schoolRepository, targetSystemProcessor)
    }

    @Bean
    fun ownedDocumentsProcessor(
        documentTypeRepository: DocumentTypeRepository,
        ownedDocumentRepository: OwnedDocumentRepository,
        citizenshipRepository: CitizenshipRepository,
        @Qualifier("phoneNumerProcessor") targetSystemProcessor: TargetSystemProcessor<Person>
    ): TargetSystemProcessor<Person> {
        return OwnedDocumentsProcessor(
            documentTypeRepository = documentTypeRepository,
            ownedDocumentRepository = ownedDocumentRepository,
            citizenshipRepository = citizenshipRepository,
            targetSystemProcessor = targetSystemProcessor
        )
    }

    @Bean
    fun phoneNumerProcessor(
        phoneNumberTypeRepository: PhoneNumberTypeRepository,
        @Qualifier("personPhotoProcessor") targetSystemProcessor: TargetSystemProcessor<Person>
    ): TargetSystemProcessor<Person> {
        return PhoneNumbersProcessor(
            phoneNumberTypeRepository = phoneNumberTypeRepository,
            targetSystemProcessor = targetSystemProcessor
        )
    }

    @Bean
    fun personPhotoProcessor(
        @Qualifier("personProcessor") targetSystemProcessor: TargetSystemProcessor<Person>
    ): TargetSystemProcessor<Person> {
        return PersonPhotoProcessor(targetSystemProcessor)
    }

    @Bean
    fun personProcessor(
        personRepository: PersonRepository,
        citizenshipRepository: CitizenshipRepository,
        schoolRepository: SchoolRepository,
        organizationalUnitRepository: OrganizationalUnitRepository,
        wkuRepository: WkuRepository,
        documentTypeRepository: DocumentTypeRepository,
        ownedDocumentRepository: OwnedDocumentRepository
    ): TargetSystemProcessor<Person> {
        return PersonProcessor(
            personRepository = personRepository,
            citizenshipRepository = citizenshipRepository,
            schoolRepository = schoolRepository,
            organizationalUnitRepository = organizationalUnitRepository,
            wkuRepository = wkuRepository,
            documentTypeRepository = documentTypeRepository,
            ownedDocumentRepository = ownedDocumentRepository
        )
    }
}
