import {ImportStatus} from "./import-status.enum";

export class ImportProgress {
  id: Number;

  importedApplications: Number;

  saveErrors: Number;

  savedApplicants: Number;

  totalCount: Number;

  importStatus: ImportStatus;

  error: String;
}
