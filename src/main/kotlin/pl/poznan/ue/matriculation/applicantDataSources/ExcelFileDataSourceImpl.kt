package pl.poznan.ue.matriculation.applicantDataSources

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import pl.poznan.ue.matriculation.excelfile.dto.Address
import pl.poznan.ue.matriculation.excelfile.dto.ExcelFileApplicantDto
import pl.poznan.ue.matriculation.excelfile.dto.ExcelFileApplicationDto
import pl.poznan.ue.matriculation.excelfile.mapper.ExcelFileApplicantMapper
import pl.poznan.ue.matriculation.excelfile.mapper.ExcelFileApplicationMapper
import pl.poznan.ue.matriculation.irk.dto.NotificationDto
import pl.poznan.ue.matriculation.kotlinExtensions.nameCapitalize
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.ProgrammeDto
import pl.poznan.ue.matriculation.local.dto.RegistrationDto
import pl.poznan.ue.matriculation.oracle.repo.ProgrammeRepository
import java.text.SimpleDateFormat


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
        val dataFile = XSSFWorkbook(import.dataFile.inputStream())
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

    override fun getPhoto(photoUrl: String): ByteArray? {
        return null
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
                given = row.getCell(0).stringCellValue.trim().nameCapitalize(),
                middle = row.getCell(1).stringCellValue.trim().nameCapitalize(),
                family = row.getCell(2).stringCellValue.trim().nameCapitalize(),
                sex = row.getCell(3).stringCellValue.trim().first(),
                email = row.getCell(4).stringCellValue.trim(),
                pesel = row.getCell(5)?.stringCellValue?.trim(),
                passport = row.getCell(6)?.stringCellValue?.trim(),
                issueCountry = row.getCell(7)?.stringCellValue?.trim(),
                issueDate = if (row.getCell(8)?.cellTypeEnum == CellType.NUMERIC) row.getCell(8)?.dateCellValue
                else row.getCell(8)?.stringCellValue?.let {
                    simpleDateFormat.parse(row.getCell(8)?.stringCellValue)
                },
                birthDate = if (row.getCell(9)?.cellTypeEnum == CellType.NUMERIC) row.getCell(9).dateCellValue
                else simpleDateFormat.parse(row.getCell(9)?.stringCellValue),
                birthPlace = row.getCell(10).stringCellValue.trim(),
                fathersName = row.getCell(11)?.stringCellValue?.trim()?.nameCapitalize(),
                mothersName = row.getCell(12)?.stringCellValue?.trim()?.nameCapitalize(),
                citizenship = row.getCell(13).stringCellValue.trim(),
                address = Address(
                    countryCode = row.getCell(14)?.stringCellValue?.trim(),
                    city = row.getCell(15)?.stringCellValue?.trim(),
                    street = row.getCell(16)?.stringCellValue?.trim(),
                    streetNumber = row.getCell(17)?.let {
                        it.setCellType(CellType.STRING)
                        row.getCell(17)?.stringCellValue?.trim()
                    },
                    flatNumber = row.getCell(18)?.let {
                        it.setCellType(CellType.STRING)
                        it.stringCellValue?.trim()
                    },
                    postalCode = row.getCell(19)?.let {
                        it.setCellType(CellType.STRING)
                        it.stringCellValue?.trim()
                    }
                ),
                phoneNumber = row.getCell(20)?.stringCellValue?.replace(" ", "")
            )
        )
    }

    private fun checkSpreadsheet(headerRow: Row): Boolean {
        return headerRow.getCell(0).stringCellValue.trim().toUpperCase() == "IMIĘ"
                && headerRow.getCell(1).stringCellValue.trim().toUpperCase() == "DRUGIE IMIĘ"
                && headerRow.getCell(2).stringCellValue.trim().toUpperCase() == "NAZWISKO"
                && headerRow.getCell(3).stringCellValue.trim().toUpperCase() == "PŁEĆ"
                && headerRow.getCell(4).stringCellValue.trim().toUpperCase() == "EMAIL"
                && headerRow.getCell(5).stringCellValue.trim().toUpperCase() == "PESEL"
                && headerRow.getCell(6).stringCellValue.trim().toUpperCase() == "NR PASZPORTU"
                && headerRow.getCell(7).stringCellValue.trim().toUpperCase() == "KRAJ WYDANIA"
                && headerRow.getCell(8).stringCellValue.trim().toUpperCase() == "DATA WAŻNOŚCI"
                && headerRow.getCell(9).stringCellValue.trim().toUpperCase() == "DATA URODZENIA"
                && headerRow.getCell(10).stringCellValue.trim().toUpperCase() == "MIEJSCE URODZENIA"
                && headerRow.getCell(11).stringCellValue.trim().toUpperCase() == "IMIĘ OJCA"
                && headerRow.getCell(12).stringCellValue.trim().toUpperCase() == "IMIĘ MATKI"
                && headerRow.getCell(13).stringCellValue.trim().toUpperCase() == "KOD OBYWATELSTWA"
                && headerRow.getCell(14).stringCellValue.trim().toUpperCase() == "KOD KRAJU ISO 3166-1 ALFA-2"
                && headerRow.getCell(15).stringCellValue.trim().toUpperCase() == "MIASTO"
                && headerRow.getCell(16).stringCellValue.trim().toUpperCase() == "ULICA"
                && headerRow.getCell(17).stringCellValue.trim().toUpperCase() == "NUMER ULICY"
                && headerRow.getCell(18).stringCellValue.trim().toUpperCase() == "NUMER MIESZKANIA"
                && headerRow.getCell(19).stringCellValue.trim().toUpperCase() == "KOD POCZTOWY"
                && headerRow.getCell(20).stringCellValue.trim().toUpperCase() == "NR TELEFONU"
    }

    override fun preprocess(applicationDto: ExcelFileApplicationDto, applicantDto: ExcelFileApplicantDto) {
    }

    override fun sendNotification(foreignApplicantId: Long, notificationDto: NotificationDto) {
        TODO("Not yet implemented")
    }
}