import {CasAssertion} from './casAssertion';
import {Authority} from './authority';

export interface User {
  authorities: Authority[];
  casAssertion: CasAssertion;
  enabled: boolean;
  username: string;
  accountNonLocked: boolean;
  accountNonExpired: boolean;
  credentialsNonExpired: boolean;
  password?: any;
}
