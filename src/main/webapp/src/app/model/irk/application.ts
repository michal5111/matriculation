import {Observable} from "rxjs";
import {Applicant} from "./applicant";

export interface Application {
  id: number,
  user: number,
  applicant: Applicant,
  payment: string,
  score: string,
  position: string,
  qualified: string,
  admitted: string,
  comment: string,
  turn: {
    programme: string,
    registration: string,
    dateFrom: Date,
    dateTo: Date
  },
  foreignerData: {
    baseOfStay: string,
    sourceOfFinancing: string,
    basisOfAdmission: string
  }
  irkInstance: string
}
