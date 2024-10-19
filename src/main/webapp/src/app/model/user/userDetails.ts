import {CasAssertion} from './casAssertion';
import {Authority} from './authority';

export interface UserDetails {
  authorities: Authority[];
  casAssertion: CasAssertion;
  enabled: boolean;
  username: string;
  accountNonLocked: boolean;
  accountNonExpired: boolean;
  credentialsNonExpired: boolean;
  password?: any;
  mail: string | null;
}
