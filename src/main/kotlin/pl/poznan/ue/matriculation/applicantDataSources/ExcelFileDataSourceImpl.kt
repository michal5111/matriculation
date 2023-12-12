package pl.poznan.ue.matriculation.applicantDataSources

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import pl.poznan.ue.matriculation.excelfile.dto.Address
import pl.poznan.ue.matriculation.excelfile.dto.ExcelFileApplicantDto
import pl.poznan.ue.matriculation.excelfile.dto.ExcelFileApplicationDto
import pl.poznan.ue.matriculation.excelfile.mapper.ExcelFileApplicantMapper
import pl.poznan.ue.matriculation.excelfile.mapper.ExcelFileApplicationMapper
import pl.poznan.ue.matriculation.kotlinExtensions.trimPhoneNumber
import pl.poznan.ue.matriculation.kotlinExtensions.trimPostalCode
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.applicants.IdentityDocument
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.dto.DataSourceAdditionalParameter
import pl.poznan.ue.matriculation.local.dto.FileDataSourceAdditionalParameter
import pl.poznan.ue.matriculation.local.dto.ProgrammeDto
import pl.poznan.ue.matriculation.local.dto.RegistrationDto
import pl.poznan.ue.matriculation.oracle.service.ProgrammeService
import java.text.SimpleDateFormat
import java.util.*


class ExcelFileDataSourceImpl(
    private val programmeService: ProgrammeService,
    private val excelFileApplicantMapper: ExcelFileApplicantMapper,
    private val excelFileApplicationMapper: ExcelFileApplicationMapper
) : IApplicationDataSource<ExcelFileApplicationDto, ExcelFileApplicantDto> {

    val logger: Logger = LoggerFactory.getLogger(ExcelFileDataSourceImpl::class.java)

    override val name = "Plik Excel"

    override val id = "EXCEL_FILE"

    override val instanceUrl = "Plik Excel"

    override val additionalParameters: List<DataSourceAdditionalParameter>
        get() = listOf(
            FileDataSourceAdditionalParameter(
                "dataFile",
                "application/vnd.ms-excel|application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                false,
                "files/importFileTemplates/szablon_importu.xlsx",
                "Plik Excel"
            )
        )

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
        val dataFileBase64 = (import.additionalProperties?.get("dataFile") as String?)?.substringAfter("base64,")
            ?: throw IllegalStateException("Data file is null")
        val importDataFile = Base64.getDecoder().decode(dataFileBase64)
        XSSFWorkbook(importDataFile.inputStream()).use {
            val sheet = it.getSheetAt(0)
            val rows = sheet.iterator()
            val header = rows.next()
            val fileHashCode = dataFileBase64.hashCode()
            while (rows.hasNext()) {
                val currentRow = rows.next()
                if (currentRow.getCellStringOrNull(SURNAME_CELL) == null) {
                    continue
                }
                excelFileApplicationDtoList += mapExcelRowToDto(currentRow, fileHashCode)
            }
        }
        return object : IPage<ExcelFileApplicationDto> {
            override fun getTotalSize(): Int {
                return excelFileApplicationDtoList.size
            }

            override fun getContent(): List<ExcelFileApplicationDto> {
                return excelFileApplicationDtoList
            }

            override fun hasNext(): Boolean {
                return false
            }
        }
    }

    override fun getApplicantById(applicantId: Long, applicationDto: ExcelFileApplicationDto): ExcelFileApplicantDto {
        return applicationDto.applicant
    }

    override fun postMatriculation(foreignApplicationId: Long): Int {
        return 1
    }

    override fun getAvailableRegistrationProgrammes(registration: String): List<ProgrammeDto> {
        return programmeService.findAll(Sort.by("code")).map {
            ProgrammeDto(
                id = it.code,
                name = "${it.code} ${it.description}",
                usosId = it.code
            )
        }
    }

    override fun getAvailableRegistrations(filter: String?): List<RegistrationDto> {
        return listOf(
            RegistrationDto(
                id = "EXCEL_FILE",
                name = "Plik Excel"
            )
        )
    }

    override fun getApplicationById(
        applicationId: Long,
        applicationDto: ExcelFileApplicationDto
    ): ExcelFileApplicationDto {
        return applicationDto
    }

    override fun mapApplicationDtoToApplication(applicationDto: ExcelFileApplicationDto): Application {
        return excelFileApplicationMapper.mapExcelFileApplicationDtoToApplication(applicationDto)
    }

    override fun mapApplicantDtoToApplicant(applicantDto: ExcelFileApplicantDto): Applicant {
        return excelFileApplicantMapper.mapExcelFileApplicantToApplicant(applicantDto)
    }

    override fun updateApplication(
        application: Application,
        applicationDto: ExcelFileApplicationDto
    ): Application {
        return excelFileApplicationMapper.updateApplicationFromExcelFileApplication(application, applicationDto)
    }

    override fun updateApplicant(
        applicant: Applicant,
        applicantDto: ExcelFileApplicantDto,
        applicationDto: ExcelFileApplicationDto
    ): Applicant {
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

    override fun getApplicationEditUrl(applicationId: Long): String? {
        return null
    }

    private fun mapExcelRowToDto(row: Row, fileHashCode: Int): ExcelFileApplicationDto {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        row.getCell(PESEL_CELL).cellType = CellType.STRING
        return ExcelFileApplicationDto(
            id = if (row.getCellStringOrNull(PESEL_CELL) != null)
                row.getCellString(PESEL_CELL).replace(" ", "").hashCode().toLong() + fileHashCode
            else
                row.getCellStringOrNull(PASSPORT_NUMBER_CELL)?.replace(" ", "").hashCode().toLong() + fileHashCode,
            applicant = ExcelFileApplicantDto(
                id = if (row.getCellStringOrNull(PESEL_CELL) != null)
                    row.getCellStringOrNull(PESEL_CELL)?.replace(" ", "").hashCode().toLong()
                else
                    row.getCellStringOrNull(PASSPORT_NUMBER_CELL)?.replace(" ", "").hashCode().toLong(),
                given = row.getCellString(NAME_CELL),
                middle = row.getCellStringOrNull(MIDDLE_NAME_CELL),
                family = row.getCellString(SURNAME_CELL),
                sex = if (row.getCellString(SEX_CELL).first().uppercaseChar() == 'K' || row.getCellString(SEX_CELL)
                        .first().uppercaseChar() == 'F'
                ) 'K' else 'M',
                email = row.getCellString(EMAIL_CELL),
                pesel = row.getCellStringOrNull(PESEL_CELL),
                passport = row.getCellStringOrNull(PASSPORT_NUMBER_CELL),
                issueCountry = row.getCellStringOrNull(PASSPORT_COUNTRY),
                issueDate = if (row.getCell(PASSPORT_VALID_DATE_CELL)?.cellType == CellType.NUMERIC)
                    row.getCell(PASSPORT_VALID_DATE_CELL)?.dateCellValue
                else row.getCellStringOrNull(PASSPORT_VALID_DATE_CELL)?.let {
                    simpleDateFormat.parse(it)
                },
                birthDate = if (row.getCell(BIRTH_DATE_CELL)?.cellType == CellType.NUMERIC)
                    row.getCell(BIRTH_DATE_CELL).dateCellValue
                else simpleDateFormat.parse(row.getCell(BIRTH_DATE_CELL)?.toString()),
                birthPlace = row.getCellString(BIRTH_PLACE_CELL),
                fathersName = row.getCellStringOrNull(FATHERS_NAME_CELL),
                mothersName = row.getCellStringOrNull(MOTHERS_NAME_CELL),
                citizenship = row.getCellString(CITIZENSHIP_CELL),
                address = Address(
                    countryCode = row.getCellStringOrNull(ADDRESS_COUNTRY_CELL),
                    city = row.getCellStringOrNull(CITY_CELL),
                    street = row.getCellStringOrNull(STREET_CELL),
                    streetNumber = row.getCellStringOrNull(STREET_NUMBER_CELL),
                    flatNumber = row.getCellStringOrNull(FLAT_NUMBER_CELL),
                    postalCode = row.getCellStringOrNull(POSTAL_CODE_CELL)?.trimPostalCode()
                ),
                phoneNumber = row.getCellStringOrNull(PHONE_NUMBER_CELL)?.trimPhoneNumber()
            )
        )
    }

    private fun Row.getCellStringOrNull(index: Int): String? {
        return this.getCell(index)?.toString()?.takeIf { it.isNotBlank() }?.trim()
    }

    private fun Row.getCellString(index: Int): String {
        return this.getCellStringOrNull(index) ?: error("Cell $index is null")
    }

    override fun getPrimaryIdentityDocument(
        application: Application,
        applicationDto: ExcelFileApplicationDto,
        applicant: Applicant,
        applicantDto: ExcelFileApplicantDto,
        import: Import
    ): IdentityDocument? {
        return applicant.identityDocuments.firstOrNull()
    }
}
