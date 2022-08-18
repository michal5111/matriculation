//package pl.poznan.ue.matriculation
//
//import org.junit.jupiter.api.Assertions
//import org.junit.jupiter.api.Test
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.TestConfiguration
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Primary
//import org.springframework.core.task.SyncTaskExecutor
//import org.springframework.data.repository.findByIdOrNull
//import org.springframework.orm.ObjectOptimisticLockingFailureException
//import org.springframework.transaction.annotation.Transactional
//import pl.poznan.ue.matriculation.local.domain.applicants.*
//import pl.poznan.ue.matriculation.local.domain.applications.Application
//import pl.poznan.ue.matriculation.local.domain.enum.AccommodationPreference
//import pl.poznan.ue.matriculation.local.domain.enum.AddressType
//import pl.poznan.ue.matriculation.local.domain.enum.DurationType
//import pl.poznan.ue.matriculation.local.domain.import.Import
//import pl.poznan.ue.matriculation.oracle.domain.Person
//import pl.poznan.ue.matriculation.oracle.repo.PersonRepository
//import pl.poznan.ue.matriculation.oracle.service.PersonProcessorService
//import java.text.SimpleDateFormat
//import java.util.*
//import java.util.concurrent.Executor
//
//class PersonProcessorServiceTest : AbstractIT() {
//
//    val logger: Logger = LoggerFactory.getLogger(PersonProcessorServiceTest::class.java)
//
//    @Autowired
//    lateinit var personProcessorService: PersonProcessorService
//
//    @Autowired
//    lateinit var personRepository: PersonRepository
//
//    val df = SimpleDateFormat("dd.MM.yyyy")
//
//    @TestConfiguration
//    class TestConfig {
//        @Bean(name = ["defaultTaskExecutor"])
//        @Primary
//        fun testTaskExecutor(): Executor {
//            return SyncTaskExecutor()
//        }
//    }
//
//    @Transactional(transactionManager = "oracleTransactionManager")
//    @Test
//    fun test() {
//        //given
//        val import = Import(
//            dateOfAddmision = Date(),
//            didacticCycleCode = "202021/SL",
//            indexPoolCode = "C",
//            programmeCode = "S1-E",
//            registration = "S1_PL_SZ_202021",
//            stageCode = "s1-S1-E",
//            startDate = Date(),
//            dataSourceId = "IRK_TEST",
//            dataSourceName = "IRK test",
//            programmeForeignId = "S1-E",
//            programmeForeignName = "S1_PL_SZ_202021",
//            indexPoolName = "Centralna",
//            additionalProperties = null
//        )
//        val testDate = Date()
//        val application = Application(
//            foreignId = 1,
//            dataSourceId = "EXCEL_FILE",
//            admitted = "addmited",
//            comment = "comment",
//            baseOfStay = "l",
//            basisOfAdmission = "PDAR",
//            sourceOfFinancing = "SAM",
//            payment = "paid",
//            position = "1",
//            qualified = "qualified",
//            score = "300",
//            editUrl = "lol",
//            import = import,
//            applicant = Applicant(
//                foreignId = 1,
//                dataSourceId = "EXCEL_FILE",
//                email = "mk@email.com",
//                indexNumber = "12345",
//                password = "",
//                given = "Michał",
//                family = "Kubiak",
//                maiden = null,
//                middle = null,
//                citizenship = "PL",
//                nationality = "PL",
//                photo = null,
//                photoPermission = "nobody",
//                sex = 'M',
//                pesel = "71041311152",
//                dateOfBirth = df.parse("13.04.1971"),
//                cityOfBirth = "Poznań",
//                countryOfBirth = "PL",
//                modificationDate = testDate,
//                fathersName = "Jan",
//                militaryCategory = "A",
//                militaryStatus = "REZERWA",
//                mothersName = "Janina",
//                wku = "Poznań",
//                applicantForeignerData = ApplicantForeignerData(
//                    baseOfStay = "OKP",
//                    foreignerStatus = HashSet(),
//                    polishCardIssueCountry = "PL",
//                    polishCardIssueDate = testDate,
//                    polishCardNumber = "123456",
//                    polishCardValidTo = testDate
//                ),
//                addresses = mutableSetOf(
//                    Address(
//                        addressType = AddressType.PERMANENT,
//                        countryCode = "PL",
//                        flatNumber = "1",
//                        city = "Poznań",
//                        postalCode = "60123",
//                        streetNumber = "1",
//                        street = "Tralala",
//                        cityIsCity = true
//                    ),
////                    Address(
////                        addressType = AddressType.CORRESPONDENCE,
////                        countryCode = "PL",
////                        flatNumber = "1",
////                        city = "Poznań",
////                        postalCode = "60123",
////                        streetNumber = "1",
////                        street = "Tralala",
////                        cityIsCity = true
////                    )
//                ),
//                phoneNumbers = HashSet(),
//                identityDocuments = HashSet(),
//                erasmusData = ErasmusData(
//                    accommodationPreference = AccommodationPreference.DORMITORY,
//                    homeInstitution = HomeInstitution(
//                        departmentName = "Lol",
//                        erasmusCode = "lol",
//                        country = "PL",
//                        address = "lalala"
//                    ),
//                    coordinatorData = CoordinatorData(
//                        email = "jan.kowalski@email.com",
//                        name = "la",
//                        phone = "123456789"
//                    ),
//                    courseData = CourseData(
//                        level = "1",
//                        name = "1",
//                        term = "1"
//                    ),
//                    type = "1",
//                    duration = DurationType.ONE_SEMESTER
//                )
//            )
//        ).apply {
//            applicant?.addDocument(
//                Document(
//                    certificateType = "M",
//                    certificateTypeCode = "M",
//                    certificateUsosCode = 'M',
//                    comment = "lalala",
//                    documentNumber = "123456",
//                    documentYear = 2000,
//                    issueCity = "Poznań",
//                    issueCountry = "PL",
//                    issueDate = testDate,
//                    issueInstitution = "123",
//                    issueInstitutionUsosCode = 1,
//                    modificationDate = testDate
//                )
//            )
//            applicant?.applications?.add(this)
//            applicant?.apply {
//                applicantForeignerData?.applicant = this
//                erasmusData?.applicant = this
//                erasmusData?.apply {
//                    homeInstitution?.erasmusData = this
//                    coordinatorData?.erasmusData = this
//                    courseData?.erasmusData = this
//                }
//                certificate = documents.first()
//            }
//        }
//        //when
//        var person: Person
//        try {
//            person = personProcessorService.process(application, import).systemId
//        } catch (e: ObjectOptimisticLockingFailureException) {
//            val exceptionPerson = (e.identifier as Long?)?.let { personRepository.getById(it) }
//            logger.info("Exception Person: {}", exceptionPerson?.modificationDate)
//            throw e
//        }
//        //then
//        person = personRepository.findByIdOrNull(person.id!!)!!
//        Assertions.assertEquals("Michał", person.name, "Name should be Michał")
//        Assertions.assertEquals("Kubiak", person.surname, "Surname should be Kubiak")
//        Assertions.assertNull(person.middleName, "Middle name should be null")
//        Assertions.assertEquals(df.parse("13.04.1971"), person.birthDate, "Birth date should be 13.04.1971")
//        Assertions.assertEquals("mk@email.com", person.privateEmail, "Email should be mk@email.com")
//        Assertions.assertEquals('M', person.sex, "Sex should be M")
//        Assertions.assertEquals("PL", person.citizenship?.code, "Citizenship code should be PL")
//        Assertions.assertEquals("PL", person.nationality?.code, "Nationality code should be PL")
//        Assertions.assertEquals("71041311152", person.pesel, "Pesel should be 71041311152")
//        Assertions.assertEquals("Poznań", person.birthCity, "Birth city should be Poznań")
//        Assertions.assertEquals("PL", person.birthCountry?.code, "Birth country code should be PL")
//        Assertions.assertEquals("Jan", person.fathersName, "Father's name should be Jan")
//        Assertions.assertEquals("Janina", person.mothersName, "Mother's name should be Janina")
//        Assertions.assertEquals("A", person.militaryCategory, "Military category should be A")
//        Assertions.assertEquals("REZERWA", person.militaryStatus, "Military status should be REZERWA")
//        Assertions.assertEquals("Poznań", person.wku?.code, "WKU code should be Poznań")
//        Assertions.assertTrue(person.personProgrammes.any {
//            it.programme.code == "S1-E"
//        }, "Should have programme S1-E")
//        Assertions.assertEquals(1, person.personProgrammes.size, "Should have 1 person programme")
//        Assertions.assertTrue(person.personProgrammes.any { personProgramme ->
//            personProgramme.personStages.any {
//                it.programmeStage.stage.code == "s1-S1-E"
//            }
//        }, "Should have stage s1-S1-E")
//        Assertions.assertEquals(1, person.personProgrammes.first().personStages.size)
//        Assertions.assertEquals(2, person.addresses.size, "Should have 2 adresses")
//        Assertions.assertTrue(person.addresses.any {
//            it.addressType.code == "STA"
//                && it.country?.code == "PL"
//                && it.city == "Poznań"
//                && it.zipCode == "60123"
//                && it.houseNumber == "1"
//                && it.flatNumber == "1"
//                && it.street == "Tralala"
//                && it.cityIsCity == true
//        })
//        Assertions.assertTrue(person.addresses.any {
//            it.addressType.code == "KOR"
//                && it.country?.code == "PL"
//                && it.city == "Poznań"
//                && it.zipCode == "60123"
//                && it.houseNumber == "1"
//                && it.flatNumber == "1"
//                && it.street == "Tralala"
//                && it.cityIsCity == true
//        })
//        Assertions.assertTrue(person.addresses.none {
//            it.city.isNullOrBlank()
//                && it.street.isNullOrBlank()
//                && it.zipCode.isNullOrBlank()
//                && it.foreignZipCode.isNullOrBlank()
//        })
//        Assertions.assertNotNull(person.personProgrammes.first().entitlementDocument)
//        Assertions.assertTrue(person.entitlementDocuments.any {
//            it.type == 'M' &&
//                it.number == "123456" &&
//                it.issueDate == testDate &&
//                it.school?.id == 1L
//        })
//        Assertions.assertEquals(1, person.entitlementDocuments.size, "Should have 1 entitlement document")
//        Assertions.assertTrue(person.personProgrammes.any {
//            it.irkApplication != null && it.irkApplication?.applicationId == application.foreignId
//        }, "Should have matriculation confirmation")
//        Assertions.assertEquals(1, person.personArrivals.size, "Should have an arrival")
//        Assertions.assertTrue(person.students.any {
//            it.mainIndex
//        }, "Should have one main index")
//        Assertions.assertEquals(0, person.personChangeHistories.size)
//    }
//}
