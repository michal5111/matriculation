import {ImportStatus} from './import-status.enum';

export class ImportProgress {
  id: number;

  importedApplications: number;

  saveErrors: number;

  savedApplicants: number;

  totalCount: number;

  importStatus: ImportStatus;

  importedUids: number;

  notificationsSend: number;

  potentialDuplicates: number;

  error: string;
}
