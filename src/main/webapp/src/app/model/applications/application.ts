import {Applicant} from './applicant';
import {Document} from './document';

export type Application = {
  id: number;
  foreignId: number;
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
    dateFrom: string;
    dateTo: string;
  };
  foreignerData: {
    baseOfStay: string;
    sourceOfFinancing: string;
    basisOfAdmission: string;
  };
  certificate: Document;
  importStatus: string;
  importError: string;
  stackTrace: string;
  irkInstance: string;
  dataSourceId: string;
  editUrl: string;
  importId: number;
};
