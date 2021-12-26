package pl.poznan.ue.matriculation.local.domain.import

import pl.poznan.ue.matriculation.local.domain.enum.ImportStatus

interface ImportProgress {
    var id: Long?

    var importedApplications: Int

    var saveErrors: Int

    var savedApplicants: Int

    var totalCount: Int?

    var importedUids: Int

    var notificationsSend: Int

    var potentialDuplicates: Int

    var importStatus: ImportStatus

    var error: String?
}

