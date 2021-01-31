import {Role} from './role';

export class User {
  id: number;
  uid: number;
  roles: Role[] = [];
}
