import {PotentialDuplicateStatus} from '../import/potential-duplicate-status.enum';

export class ApplicantUsosIdAndPotentialDuplicateStatusDto {
  usosId: number;
  potentialDuplicateStatus: PotentialDuplicateStatus;

  constructor(usosId: number, potentialDuplicateStatus: PotentialDuplicateStatus) {
    this.usosId = usosId;
    this.potentialDuplicateStatus = potentialDuplicateStatus;
  }
}
