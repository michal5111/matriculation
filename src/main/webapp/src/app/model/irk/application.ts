import {Applicant} from './applicant';
import {Document} from "./document";
import {ImportStatus} from "../import/import-status.enum";

export interface Application {
  id: number;
  irkId: number;
  user: number;
  applicant: Applicant;
  payment: string;
  score: string;
  position: string;
  qualified: string;
  admitted: string;
  comment: string;
  turn: {
    programme: string;
    registration: string;
    dateFrom: Date;
    dateTo: Date;
  };
  foreignerData: {
    baseOfStay: string;
    sourceOfFinancing: string;
    basisOfAdmission: string;
  };
  certificate: Document;
  importStatus: ImportStatus;
  importError: string;
  stackTrace: string;
  irkInstance: string;
}
