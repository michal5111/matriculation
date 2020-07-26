package pl.poznan.ue.matriculation.local.dto

import pl.poznan.ue.matriculation.applicantDataSources.IForeignApplicantIdGettable
import pl.poznan.ue.matriculation.applicantDataSources.IForeignIdGettable
import java.io.Serializable

abstract class AbstractApplicationDto : IForeignApplicantIdGettable, IForeignIdGettable, Serializable