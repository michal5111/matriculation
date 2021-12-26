import {ImportStatus} from './import-status.enum';

export class Import {

  id: number;
  programmeCode: string;
  programmeForeignId: string;
  stageCode: string;
  registration: string;
  indexPoolCode: string;
  startDate: Date;
  dateOfAddmision: Date;
  didacticCycleCode: string;
  dataSourceId: string;
  dataFile: string;
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
