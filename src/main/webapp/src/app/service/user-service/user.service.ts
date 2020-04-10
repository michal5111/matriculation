import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {User} from "../../model/user/user";
import {tap} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  userUrl = "/api/user";
  user: User
  isAuthenticated = false;

  constructor(private http: HttpClient) { }

  getUser() {
    return this.http.get<User>(this.userUrl).pipe(
      tap(user => this.user = user),
      tap(user => this.isAuthenticated = !!user.casAssertion)
    )
  }
}
