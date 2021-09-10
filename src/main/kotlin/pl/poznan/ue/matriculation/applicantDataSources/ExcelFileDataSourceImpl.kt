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

    override fun getApplicationsPage(
        import: Import,
        registrationCode: String,
        programmeForeignId: String,
        pageNumber: Int
    ): IPage<ExcelFileApplicationDto> {
        val excelFileApplicationDtoList: MutableList<ExcelFileApplicationDto> = mutableListOf()
        if (import.dataFile == null) {
            throw IllegalStateException("Data file is null")
        }
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
            id = if (row.getCell(5)?.stringCellValue != null)
                row.getCell(5).stringCellValue.replace(" ", "").hashCode().toLong() + fileHashCode
            else
                row.getCell(6)?.stringCellValue?.replace(" ", "").hashCode().toLong() + fileHashCode,
            applicant = ExcelFileApplicantDto(
                id = if (row.getCell(5)?.stringCellValue != null)
                    row.getCell(5)?.stringCellValue?.replace(" ", "").hashCode().toLong()
                else
                    row.getCell(6)?.stringCellValue?.replace(" ", "").hashCode().toLong(),
                given = row.getCell(0).stringCellValue.nameCapitalize(),
                middle = row.getCell(1).stringCellValue.nameCapitalize(),
                family = row.getCell(2).stringCellValue.nameCapitalize(),
                sex = row.getCell(3).stringCellValue.trim().first(),
                email = row.getCell(4).stringCellValue.trim(),
                pesel = row.getCell(5)?.stringCellValue?.trim(),
                passport = row.getCell(6)?.stringCellValue?.trim(),
                issueCountry = row.getCell(7)?.stringCellValue?.trim(),
                issueDate = if (row.getCell(8)?.cellType == CellType.NUMERIC) row.getCell(8)?.dateCellValue
                else row.getCell(8)?.stringCellValue?.let {
                    simpleDateFormat.parse(row.getCell(8)?.stringCellValue)
                },
                birthDate = if (row.getCell(9)?.cellType == CellType.NUMERIC) row.getCell(9).dateCellValue
                else simpleDateFormat.parse(row.getCell(9)?.stringCellValue),
                birthPlace = row.getCell(10).stringCellValue.trim(),
                fathersName = row.getCell(11)?.stringCellValue?.nameCapitalize(),
                mothersName = row.getCell(12)?.stringCellValue?.nameCapitalize(),
                citizenship = row.getCell(13).stringCellValue.trim(),
                address = Address(
                    countryCode = row.getCell(14)?.stringCellValue?.trim(),
                    city = row.getCell(15)?.stringCellValue?.trim(),
                    street = row.getCell(16)?.stringCellValue?.trim(),
                    streetNumber = row.getCell(17)?.let {
                        row.getCell(17)?.stringCellValue?.trim()
                    },
                    flatNumber = row.getCell(18)?.let {
                        it.stringCellValue?.trim()
                    },
                    postalCode = row.getCell(19)?.let {
                        it.stringCellValue?.trimPostalCode()
                    }
                ),
                phoneNumber = row.getCell(20)?.stringCellValue?.trimPhoneNumber()
            )
        )
    }

    private fun checkSpreadsheet(headerRow: Row): Boolean {
        return headerRow.getCell(0).stringCellValue.trim().uppercase(Locale.getDefault()) == "IMIĘ"
            && headerRow.getCell(1).stringCellValue.trim().uppercase(Locale.getDefault()) == "DRUGIE IMIĘ"
            && headerRow.getCell(2).stringCellValue.trim().uppercase(Locale.getDefault()) == "NAZWISKO"
            && headerRow.getCell(3).stringCellValue.trim().uppercase(Locale.getDefault()) == "PŁEĆ"
            && headerRow.getCell(4).stringCellValue.trim().uppercase(Locale.getDefault()) == "EMAIL"
            && headerRow.getCell(5).stringCellValue.trim().uppercase(Locale.getDefault()) == "PESEL"
            && headerRow.getCell(6).stringCellValue.trim().uppercase(Locale.getDefault()) == "NR PASZPORTU"
            && headerRow.getCell(7).stringCellValue.trim().uppercase(Locale.getDefault()) == "KRAJ WYDANIA"
            && headerRow.getCell(8).stringCellValue.trim().uppercase(Locale.getDefault()) == "DATA WAŻNOŚCI"
            && headerRow.getCell(9).stringCellValue.trim().uppercase(Locale.getDefault()) == "DATA URODZENIA"
            && headerRow.getCell(10).stringCellValue.trim().uppercase(Locale.getDefault()) == "MIEJSCE URODZENIA"
            && headerRow.getCell(11).stringCellValue.trim().uppercase(Locale.getDefault()) == "IMIĘ OJCA"
            && headerRow.getCell(12).stringCellValue.trim().uppercase(Locale.getDefault()) == "IMIĘ MATKI"
            && headerRow.getCell(13).stringCellValue.trim().uppercase(Locale.getDefault()) == "KOD OBYWATELSTWA"
            && headerRow.getCell(14).stringCellValue.trim()
            .uppercase(Locale.getDefault()) == "KOD KRAJU ISO 3166-1 ALFA-2"
            && headerRow.getCell(15).stringCellValue.trim().uppercase(Locale.getDefault()) == "MIASTO"
            && headerRow.getCell(16).stringCellValue.trim().uppercase(Locale.getDefault()) == "ULICA"
            && headerRow.getCell(17).stringCellValue.trim().uppercase(Locale.getDefault()) == "NUMER ULICY"
            && headerRow.getCell(18).stringCellValue.trim().uppercase(Locale.getDefault()) == "NUMER MIESZKANIA"
            && headerRow.getCell(19).stringCellValue.trim().uppercase(Locale.getDefault()) == "KOD POCZTOWY"
            && headerRow.getCell(20).stringCellValue.trim().uppercase(Locale.getDefault()) == "NR TELEFONU"
    }

    override fun preprocess(applicationDto: ExcelFileApplicationDto, applicantDto: ExcelFileApplicantDto) {
    }
}
