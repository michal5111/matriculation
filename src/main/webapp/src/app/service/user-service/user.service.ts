import {Inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../../model/user/user';
import {tap} from 'rxjs/operators';
import {APP_BASE_HREF} from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  userUrl = `${this.baseHref}api/user`;
  user: User;
  isAuthenticated = false;

  constructor(@Inject(APP_BASE_HREF) public baseHref: string, private http: HttpClient) { }

  getUser() {
    return this.http.get<User>(this.userUrl).pipe(
      tap(user => this.user = user),
      tap(user => this.isAuthenticated = !!user.casAssertion)
    );
  }
}
