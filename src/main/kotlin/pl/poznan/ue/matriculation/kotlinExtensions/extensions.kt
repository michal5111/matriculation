package pl.poznan.ue.matriculation.kotlinExtensions

import org.springframework.data.domain.Page
import pl.poznan.ue.matriculation.local.domain.applicants.Applicant
import pl.poznan.ue.matriculation.local.domain.applicants.Document
import pl.poznan.ue.matriculation.local.domain.applicants.IdentityDocument
import pl.poznan.ue.matriculation.local.domain.applications.Application
import pl.poznan.ue.matriculation.local.domain.import.Import
import pl.poznan.ue.matriculation.local.domain.user.User
import pl.poznan.ue.matriculation.local.dto.*
import pl.poznan.ue.matriculation.local.service.AsyncService
import java.sql.Blob
import java.time.LocalDate
import javax.sql.rowset.serial.SerialBlob
import javax.sql.rowset.serial.SerialClob

fun Exception.stackTraceToHtmlString(): String = this.stackTraceToString()
    .replace("\n", "<br>")
    .replace("\t", "&emsp;")

//fun String.nameCapitalize(): String {
//    return this.trim().lowercase(Locale.getDefault()).split(" -").joinToString(" ") { s ->
//        s.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
//    }
//}

inline fun <T> MutableList<T>.mutate(mutator: (T) -> Unit) {
    val iterate = this.listIterator()
    while (iterate.hasNext()) {
        val item = iterate.next()
        mutator(item)
    }
}

inline fun <T> Iterable<T>.forEachAsync(asyncService: AsyncService, crossinline action: (T) -> Unit) {
    for (element in this) asyncService.doAsync {
        action(element)
    }
}

inline fun retry(
    maxRetry: Int = 1,
    retryOn: Array<Class<out Throwable>> = arrayOf(Exception::class.java),
    action: (retryCount: Int) -> Unit
) {
    var retryCount = 0
    while (true) {
        try {
            action(retryCount)
            break
        } catch (e: Exception) {
            if (retryOn.none { e::class == it || it.isAssignableFrom(e::class.java) } || ++retryCount == maxRetry) throw e
        }
    }
}

fun ByteArray.toSerialBlob(): SerialBlob {
    return SerialBlob(this)
}

fun CharArray.toSerialClob(): SerialClob {
    return SerialClob(this)
}

fun String.toSerialClob(): SerialClob {
    return SerialClob(this.toCharArray())
}

fun Blob?.toByteArray(): ByteArray? {
    return this?.getBytes(1, length().toInt())
}

fun String.trimPostalCode() = this.replace("[^0-9]".toRegex(), "").ifBlank { null }

fun String.trimPhoneNumber() = this.replace("[^0-9+]".toRegex(), "").ifBlank { null }

data class ParsedAddress(val street: String?, val streetNumber: String?, val flatNumber: String?)

fun parseAddressFromString(address: String?): ParsedAddress {
    val addressStreetAndFlatNumber = address
        ?.substringAfterLast(' ', "")
        ?.takeIf { it.matches(".*\\d.*".toRegex()) }?.split('/')
    val streetNumber = addressStreetAndFlatNumber?.getOrNull(0)
    val flatNumber = addressStreetAndFlatNumber?.getOrNull(1)
    var street = address?.takeIf { it.isNotBlank() }
    if (streetNumber != null) {
        street = street?.substringBeforeLast(' ')?.takeIf { it.isNotBlank() }
    }
    return ParsedAddress(street = street, streetNumber = streetNumber, flatNumber = flatNumber)
}

fun User.toUserDto() = let {
    UserDto(
        id = it.id,
        uid = it.uid,
        roles = it.roles.map { role ->
            RoleDto(
                code = role.code,
                name = role.name
            )
        },
        givenName = it.givenName,
        surname = it.surname,
        email = it.email,
        usosId = it.usosId,
        version = it.version
    )
}

fun <T, Z> Page<T>.toPageDto(mapping: (T) -> Z): PageDto<Z> {
    return PageDto(
        content = content.map(mapping),
        number = number,
        totalElements = totalElements,
        totalPages = totalPages,
        size = size
    )
}

fun <T> Page<T>.toPageDto(): PageDto<T> {
    return PageDto(
        content = content,
        number = number,
        totalElements = totalElements,
        totalPages = totalPages,
        size = size
    )
}

fun Import.toDto(): ImportDto {
    return ImportDto(
        id = id,
        programmeCode = programmeCode,
        programmeForeignId = programmeForeignId,
        programmeForeignName = programmeForeignName,
        registration = registration,
        indexPoolCode = indexPoolCode,
        indexPoolName = indexPoolName,
        startDate = startDate,
        dateOfAddmision = dateOfAddmision,
        stageCode = stageCode,
        didacticCycleCode = didacticCycleCode,
        dataSourceId = dataSourceId,
        dataSourceName = dataSourceName,
        additionalProperties = additionalProperties,
        importedApplications = importedApplications,
        saveErrors = saveErrors,
        savedApplicants = savedApplicants,
        totalCount = totalCount,
        importedUids = importedUids,
        notificationsSend = notificationsSend,
        potentialDuplicates = potentialDuplicates,
        importStatus = importStatus,
        error = error,
        stackTrace = stackTrace,
    )
}

fun Application.toDto(): ApplicationDto {
    return ApplicationDto(
        id = id,
        foreignId = foreignId,
        dataSourceId = dataSourceId,
        editUrl = editUrl,
        certificate = certificate?.toDto(),
        applicant = applicant?.toDto(),
        importStatus = importStatus,
        importError = importError,
        stackTrace = stackTrace,
        baseOfStay = baseOfStay,
        basisOfAdmission = basisOfAdmission,
        sourceOfFinancing = sourceOfFinancing,
        notificationSent = notificationSent,
        warnings = warnings,
        importId = import?.id
    )
}

fun Document.toDto(): DocumentDto {
    return DocumentDto(
        applicantId = applicant?.id,
        certificateType = certificateType,
        certificateTypeCode = certificateTypeCode,
        certificateUsosCode = certificateUsosCode,
        comment = comment,
        documentNumber = documentNumber,
        documentYear = documentYear,
        issueCity = issueCity,
        issueCountry = issueCountry,
        issueDate = issueDate,
        issueInstitution = issueInstitution,
        issueInstitutionUsosCode = issueInstitutionUsosCode,
        modificationDate = modificationDate,
    )
}

fun Applicant.toDto(): ApplicantDto {
    return ApplicantDto(
        id = id,
        foreignId = foreignId,
        dataSourceId = dataSourceId,
        email = email,
        indexNumber = indexNumber,
        password = password,
        citizenship = citizenship,
        nationality = nationality,
        photo = photo,
        photoPermission = photoPermission,
        modificationDate = modificationDate,
        usosId = usosId,
        assignedIndexNumber = assignedIndexNumber,
        potentialDuplicateStatus = potentialDuplicateStatus,
        uid = uid,
        fathersName = fathersName,
        militaryCategory = militaryCategory,
        militaryStatus = militaryStatus,
        mothersName = mothersName,
        wku = wku,
        sex = sex,
        pesel = pesel,
        dateOfBirth = dateOfBirth,
        cityOfBirth = cityOfBirth,
        countryOfBirth = countryOfBirth,
        highSchoolCity = highSchoolCity,
        highSchoolName = highSchoolName,
        highSchoolType = highSchoolType,
        highSchoolUsosCode = highSchoolUsosCode,
        middle = middle,
        family = family,
        given = given,
        maiden = maiden,
        personExisted = personExisted,
        primaryIdentityDocument = primaryIdentityDocument?.toDto(),
    )
}

fun IdentityDocument.toDto(): IdentityDocumentDto {
    return IdentityDocumentDto(id = id, country = country, expDate = expDate, number = number, type = type)
}

fun Collection<IdentityDocument>.active(): List<IdentityDocument> {
    return filter {
        val expDate = it.expDate
        if (expDate != null) expDate >= LocalDate.now() else true
    }
}
