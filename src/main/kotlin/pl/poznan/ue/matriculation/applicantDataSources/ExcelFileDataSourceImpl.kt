package pl.poznan.ue.matriculation.applicantDataSources

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import pl.poznan.ue.matriculation.excelfile.dto.Address
import pl.poznan.ue.matriculation.excelfile.dto.ExcelFileApplicantDto
import pl.poznan.ue.matriculation.excelfile.dto.ExcelFileApplicationDto
import pl.poznan.ue.matriculation.excelfile.mapper.ExcelFileApplicantMapper
import pl.poznan.ue.matriculation.excelfile.mapper.ExcelFileApplicationMapper
import pl.poznan.ue.matriculation.kotlinExtensions.nameCapitalize
import pl.poznan.ue.matriculation.kotlinExtensions.trimPhoneNumber
import pl.poznan.ue.matriculation.kotlinExtensions.trimPostalCode
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.ProgrammeDto
import pl.poznan.ue.matriculation.local.dto.RegistrationDto
import pl.poznan.ue.matriculation.oracle.repo.ProgrammeRepository
import java.text.SimpleDateFormat
import java.util.*


class ExcelFileDataSourceImpl(
    private val programmeRepository: ProgrammeRepository,
    private val excelFileApplicantMapper: ExcelFileApplicantMapper,
    private val excelFileApplicationMapper: ExcelFileApplicationMapper
) : IApplicationDataSource<ExcelFileApplicationDto, ExcelFileApplicantDto> {

    private var lastPage: IPage<ExcelFileApplicationDto>? = null

    companion object {
        const val NAME_CELL = 0
        const val MIDDLE_NAME_CELL = 1
        const val SURNAME_CELL = 2
        const val SEX_CELL = 3
        const val EMAIL_CELL = 4
        const val PESEL_CELL = 5
        const val PASSPORT_NUMBER_CELL = 6
        const val PASSPORT_COUNTRY = 7
        const val PASSPORT_VALID_DATE_CELL = 8
        const val BIRTH_DATE_CELL = 9
        const val BIRTH_PLACE_CELL = 10
        const val FATHERS_NAME_CELL = 11
        const val MOTHERS_NAME_CELL = 12
        const val CITIZENSHIP_CELL = 13
        const val ADDRESS_COUNTRY_CELL = 14
        const val CITY_CELL = 15
        const val STREET_CELL = 16
        const val STREET_NUMBER_CELL = 17
        const val FLAT_NUMBER_CELL = 18
        const val POSTAL_CODE_CELL = 19
        const val PHONE_NUMBER_CELL = 20
    }

    override fun getApplicationsPage(
        import: Import,
        registrationCode: String,
        programmeForeignId: String,
        pageNumber: Int
    ): IPage<ExcelFileApplicationDto> {
        val excelFileApplicationDtoList: MutableList<ExcelFileApplicationDto> = mutableListOf()
        import.dataFile ?: throw IllegalStateException("Data file is null")
        val dataFile = XSSFWorkbook(import.dataFile?.inputStream())
        val sheet = dataFile.getSheet("Arkusz1")
        val rows = sheet.iterator()
        val header = rows.next()
        if (!checkSpreadsheet(header)) {
            throw IllegalArgumentException("Plik nie pasuje do szablonu")
        }
        val fileHashCode = import.dataFile.hashCode()
        while (rows.hasNext()) {
            val currentRow = rows.next()
            excelFileApplicationDtoList += mapExcelRowToDto(currentRow, fileHashCode)
        }
        val page = object : IPage<ExcelFileApplicationDto> {
            override fun getSize(): Int {
                return excelFileApplicationDtoList.size
            }

            override fun getResultsList(): List<ExcelFileApplicationDto> {
                return excelFileApplicationDtoList
            }

            override fun hasNext(): Boolean {
                return false
            }

        }
        lastPage = page
        dataFile.close()
        return page
    }

    override fun getApplicantById(applicantId: Long): ExcelFileApplicantDto {
        return lastPage?.getResultsList()?.find {
            it.applicant.id == applicantId
        }?.applicant ?: throw IllegalStateException("Unable to find applicant")
    }

    override val name = "Plik Excel"

    override val id = "EXCEL_FILE"

    override val instanceUrl = "Plik Excel"

    override fun postMatriculation(foreignApplicationId: Long): Int {
        return 1
    }

    override fun getAvailableRegistrationProgrammes(registration: String): List<ProgrammeDto> {
        return programmeRepository.findAll().map {
            ProgrammeDto(
                id = it.code,
                name = "${it.code} ${it.description}",
                usosId = it.code
            )
        }
    }

    override fun getAvailableRegistrations(): List<RegistrationDto> {
        return listOf(
            RegistrationDto(
                id = "EXCEL_FILE",
                name = "Plik Excel"
            )
        )
    }

    override fun getApplicationById(applicationId: Long): ExcelFileApplicationDto {
        return lastPage?.getResultsList()?.find {
            it.id == applicationId
        } ?: throw IllegalStateException("Unable to find application")
    }

    override fun mapApplicationDtoToApplication(applicationDto: ExcelFileApplicationDto): Application {
        return excelFileApplicationMapper.mapExcelFileApplicationDtoToApplication(applicationDto)
    }

    override fun mapApplicantDtoToApplicant(applicantDto: ExcelFileApplicantDto): Applicant {
        return excelFileApplicantMapper.mapExcelFileApplicantToApplicant(applicantDto)
    }

    override fun updateApplication(application: Application, applicationDto: ExcelFileApplicationDto): Application {
        return excelFileApplicationMapper.updateApplicationFromExcelFileApplication(application, applicationDto)
    }

    override fun updateApplicant(applicant: Applicant, applicantDto: ExcelFileApplicantDto): Applicant {
        return excelFileApplicantMapper.updateApplicantFromExcelApplicantDto(applicant, applicantDto)
    }

    override fun getPrimaryCertificate(
        application: Application,
        applicationDto: ExcelFileApplicationDto,
        applicant: Applicant,
        applicantDto: ExcelFileApplicantDto,
        import: Import
    ): Document? {
        return null
    }

    override fun getApplicationEditUrl(applicationId: Long): String {
        return ""
    }

    private fun mapExcelRowToDto(row: Row, fileHashCode: Int): ExcelFileApplicationDto {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        return ExcelFileApplicationDto(
            id = if (row.getCell(PESEL_CELL)?.stringCellValue != null)
                row.getCell(PESEL_CELL).stringCellValue.replace(" ", "").hashCode().toLong() + fileHashCode
            else
                row.getCell(6)?.stringCellValue?.replace(" ", "").hashCode().toLong() + fileHashCode,
            applicant = ExcelFileApplicantDto(
                id = if (row.getCell(PESEL_CELL)?.stringCellValue != null)
                    row.getCell(PESEL_CELL)?.stringCellValue?.replace(" ", "").hashCode().toLong()
                else
                    row.getCell(PASSPORT_NUMBER_CELL)?.stringCellValue?.replace(" ", "").hashCode().toLong(),
                given = row.getCell(NAME_CELL).stringCellValue.nameCapitalize(),
                middle = row.getCell(MIDDLE_NAME_CELL).stringCellValue.nameCapitalize(),
                family = row.getCell(SURNAME_CELL).stringCellValue.nameCapitalize(),
                sex = row.getCell(SEX_CELL).stringCellValue.trim().first(),
                email = row.getCell(EMAIL_CELL).stringCellValue.trim(),
                pesel = row.getCell(PESEL_CELL)?.stringCellValue?.trim(),
                passport = row.getCell(PASSPORT_NUMBER_CELL)?.stringCellValue?.trim(),
                issueCountry = row.getCell(PASSPORT_COUNTRY)?.stringCellValue?.trim(),
                issueDate = if (row.getCell(PASSPORT_VALID_DATE_CELL)?.cellType == CellType.NUMERIC) row.getCell(
                    PASSPORT_VALID_DATE_CELL
                )?.dateCellValue
                else row.getCell(PASSPORT_VALID_DATE_CELL)?.stringCellValue?.let {
                    simpleDateFormat.parse(row.getCell(PASSPORT_VALID_DATE_CELL)?.stringCellValue)
                },
                birthDate = if (row.getCell(BIRTH_DATE_CELL)?.cellType == CellType.NUMERIC) row.getCell(BIRTH_DATE_CELL).dateCellValue
                else simpleDateFormat.parse(row.getCell(BIRTH_DATE_CELL)?.stringCellValue),
                birthPlace = row.getCell(BIRTH_PLACE_CELL).stringCellValue.trim(),
                fathersName = row.getCell(FATHERS_NAME_CELL)?.stringCellValue?.nameCapitalize(),
                mothersName = row.getCell(MOTHERS_NAME_CELL)?.stringCellValue?.nameCapitalize(),
                citizenship = row.getCell(CITIZENSHIP_CELL).stringCellValue.trim(),
                address = Address(
                    countryCode = row.getCell(ADDRESS_COUNTRY_CELL)?.stringCellValue?.trim(),
                    city = row.getCell(CITY_CELL)?.stringCellValue?.trim(),
                    street = row.getCell(STREET_CELL)?.stringCellValue?.trim(),
                    streetNumber = row.getCell(STREET_NUMBER_CELL)?.let {
                        row.getCell(STREET_NUMBER_CELL)?.stringCellValue?.trim()
                    },
                    flatNumber = row.getCell(FLAT_NUMBER_CELL)?.let {
                        it.stringCellValue?.trim()
                    },
                    postalCode = row.getCell(POSTAL_CODE_CELL)?.let {
                        it.stringCellValue?.trimPostalCode()
                    }
                ),
                phoneNumber = row.getCell(PHONE_NUMBER_CELL)?.stringCellValue?.trimPhoneNumber()
            )
        )
    }

    private fun checkSpreadsheet(headerRow: Row): Boolean {
        return headerRow.getCell(NAME_CELL).stringCellValue.trim().uppercase(Locale.getDefault()) == "IMIĘ"
            && headerRow.getCell(MIDDLE_NAME_CELL).stringCellValue.trim()
            .uppercase(Locale.getDefault()) == "DRUGIE IMIĘ"
            && headerRow.getCell(SURNAME_CELL).stringCellValue.trim().uppercase(Locale.getDefault()) == "NAZWISKO"
            && headerRow.getCell(SEX_CELL).stringCellValue.trim().uppercase(Locale.getDefault()) == "PŁEĆ"
            && headerRow.getCell(EMAIL_CELL).stringCellValue.trim().uppercase(Locale.getDefault()) == "EMAIL"
            && headerRow.getCell(PESEL_CELL).stringCellValue.trim().uppercase(Locale.getDefault()) == "PESEL"
            && headerRow.getCell(PASSPORT_NUMBER_CELL).stringCellValue.trim()
            .uppercase(Locale.getDefault()) == "NR PASZPORTU"
            && headerRow.getCell(PASSPORT_COUNTRY).stringCellValue.trim()
            .uppercase(Locale.getDefault()) == "KRAJ WYDANIA"
            && headerRow.getCell(PASSPORT_VALID_DATE_CELL).stringCellValue.trim()
            .uppercase(Locale.getDefault()) == "DATA WAŻNOŚCI"
            && headerRow.getCell(BIRTH_DATE_CELL).stringCellValue.trim()
            .uppercase(Locale.getDefault()) == "DATA URODZENIA"
            && headerRow.getCell(BIRTH_PLACE_CELL).stringCellValue.trim()
            .uppercase(Locale.getDefault()) == "MIEJSCE URODZENIA"
            && headerRow.getCell(FATHERS_NAME_CELL).stringCellValue.trim().uppercase(Locale.getDefault()) == "IMIĘ OJCA"
            && headerRow.getCell(MOTHERS_NAME_CELL).stringCellValue.trim()
            .uppercase(Locale.getDefault()) == "IMIĘ MATKI"
            && headerRow.getCell(CITIZENSHIP_CELL).stringCellValue.trim()
            .uppercase(Locale.getDefault()) == "KOD OBYWATELSTWA"
            && headerRow.getCell(ADDRESS_COUNTRY_CELL).stringCellValue.trim()
            .uppercase(Locale.getDefault()) == "KOD KRAJU ISO 3166-1 ALFA-2"
            && headerRow.getCell(CITY_CELL).stringCellValue.trim().uppercase(Locale.getDefault()) == "MIASTO"
            && headerRow.getCell(STREET_CELL).stringCellValue.trim().uppercase(Locale.getDefault()) == "ULICA"
            && headerRow.getCell(STREET_NUMBER_CELL).stringCellValue.trim()
            .uppercase(Locale.getDefault()) == "NUMER ULICY"
            && headerRow.getCell(FLAT_NUMBER_CELL).stringCellValue.trim()
            .uppercase(Locale.getDefault()) == "NUMER MIESZKANIA"
            && headerRow.getCell(POSTAL_CODE_CELL).stringCellValue.trim()
            .uppercase(Locale.getDefault()) == "KOD POCZTOWY"
            && headerRow.getCell(PHONE_NUMBER_CELL).stringCellValue.trim()
            .uppercase(Locale.getDefault()) == "NR TELEFONU"
    }

    override fun preprocess(applicationDto: ExcelFileApplicationDto, applicantDto: ExcelFileApplicantDto) {
    }
}
