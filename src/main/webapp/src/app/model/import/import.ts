import {ImportStatus} from './import-status.enum';

export type Import = {
  id: number | null;
  programmeCode: string | null;
  programmeForeignId: string | null;
  programmeForeignName: string | null;
  stageCode: string | null;
  registration: string | null;
  indexPoolCode: string | null;
  indexPoolName: string | null;
  startDate: string | null;
  dateOfAddmision: string | null;
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
};
