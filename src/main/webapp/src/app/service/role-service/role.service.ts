import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {APP_BASE_HREF} from '@angular/common';
import {Role} from '../../model/user/role';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  roleUrl = `${this.baseHref}api/role`;

  constructor(@Inject(APP_BASE_HREF) public baseHref: string, private http: HttpClient) {
  }

  getRoles() {
    return this.http.get<Role[]>(this.roleUrl, httpOptions);
  }
}
