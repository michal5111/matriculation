import {ImportProgress} from './import-progress';

export class Import {

  id: number;

  programmeCode: string;

  stageCode: string;

  registration: string;

  indexPoolCode: string;

  startDate: Date;

  dateOfAddmision: Date;

  didacticCycleCode: string;

  importProgress: ImportProgress;
}
