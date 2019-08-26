import {Principal} from "./principal";

export interface CasAssertion {
  validFromDate: Date;
  validUntilDate?: any;
  authenticationDate: Date;
  attributes: any;
  principal: Principal;
  valid: boolean;
}
