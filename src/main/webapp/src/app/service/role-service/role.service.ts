import {inject, Injectable} from '@angular/core';
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
  baseHref = inject(APP_BASE_HREF);
  private http = inject(HttpClient);


  roleUrl = `${this.baseHref}api/role`;

  getRoles() {
    return this.http.get<Role[]>(this.roleUrl, httpOptions);
  }
}
