import {Role} from './role';

export class User {
  id: number | null;
  uid: number | null;
  givenName: string | null;
  surname: string | null;
  email: string | null;
  roles: Role[] = [];

  constructor() {
    this.id = null;
    this.uid = null;
    this.givenName = null;
    this.surname = null;
    this.email = null;
  }
}
