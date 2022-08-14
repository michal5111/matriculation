import {ImportStatus} from './import-status.enum';

export class Import {

  id: number | null;
  programmeCode: string | null;
  programmeForeignId: string | null;
  programmeForeignName: string | null;
  stageCode: string | null;
  registration: string | null;
  indexPoolCode: string | null;
  indexPoolName: string | null;
  startDate: Date | null;
  dateOfAddmision: Date | null;
  didacticCycleCode: string | null;
  dataSourceId: string | null;
  dataSourceName: string | null;
  additionalProperties: {};
  importedApplications: number;
  saveErrors: number;
  savedApplicants: number;
  totalCount: number | null;
  importStatus: ImportStatus | null;
  importedUids: number;
  notificationsSend: number;
  potentialDuplicates: number;
  error: string | null;
  stackTrace: string | null;

  constructor() {
    this.id = null;
    this.programmeCode = null;
    this.programmeForeignId = null;
    this.programmeForeignName = null;
    this.stageCode = null;
    this.registration = null;
    this.indexPoolCode = null;
    this.indexPoolName = null;
    this.startDate = null;
    this.dateOfAddmision = null;
    this.didacticCycleCode = null;
    this.dataSourceId = null;
    this.dataSourceName = null;
    this.additionalProperties = {};
    this.importedApplications = 0;
    this.saveErrors = 0;
    this.savedApplicants = 0;
    this.totalCount = null;
    this.importStatus = null;
    this.importedUids = 0;
    this.notificationsSend = 0;
    this.potentialDuplicates = 0;
    this.error = null;
    this.stackTrace = null;
  }
}
