import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  userUrl = "/api/user";

  constructor(private http: HttpClient) { }

  getUser() {
    return this.http.get(this.userUrl)
  }
}
