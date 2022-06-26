import {Role} from './role';

export class User {
  id: number;
  uid: number;
  givenName: string;
  surname: string;
  email: string;
  roles: Role[] = [];
}
