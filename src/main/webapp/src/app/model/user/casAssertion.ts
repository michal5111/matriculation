import {Principal} from './principal';

export interface CasAssertion {
  validFromDate: string;
  validUntilDate?: any;
  authenticationDate: string;
  attributes: any;
  principal: Principal;
  valid: boolean;
}
