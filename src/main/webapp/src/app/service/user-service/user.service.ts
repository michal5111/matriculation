import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {UserDetails} from '../../model/user/userDetails';
import {tap} from 'rxjs/operators';
import {APP_BASE_HREF} from '@angular/common';
import {User} from '../../model/user/user';
import {Observable} from 'rxjs';
import {Page} from '../../model/oracle/page/page';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class UserService {

  userUrl = `${this.baseHref}api/user`;
  user: UserDetails;
  isAuthenticated = false;

  constructor(@Inject(APP_BASE_HREF) public baseHref: string, private http: HttpClient) {
  }

  getUser() {
    return this.http.get<UserDetails>(this.userUrl, httpOptions).pipe(
      tap(user => this.user = user),
      tap(user => this.isAuthenticated = !!user.casAssertion)
    );
  }

  update(user: User): Observable<User> {
    return this.http.put<User>(this.userUrl, user, httpOptions);
  }

  create(user: User): Observable<User> {
    return this.http.post<User>(this.userUrl, user, httpOptions);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.userUrl}/${id}`);
  }

  getAll(page: number, size: number, sort?: string, sortDir?: string): Observable<Page<User>> {
    if (sort && sortDir) {
      return this.http.get<Page<User>>(`${this.userUrl}s?page=${page}&size=${size}&sort=${sort},${sortDir}`, httpOptions);
    }
    return this.http.get<Page<User>>(`${this.userUrl}s?page=${page}&size=${size}`, httpOptions);
  }

  hasRole(role: string): boolean {
    if (this.user === undefined || this.user.authorities === undefined) {
      return false;
    }
    return this.user.authorities.find(x => x.authority === role) !== undefined;
  }
}
