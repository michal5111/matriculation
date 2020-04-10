import { Component } from '@angular/core';
import { UserService } from "./service/user-service/user.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {
  title = 'angularmatriculation';

  constructor(public _userService: UserService, public _router: Router) {
    this.getUser()
  }

  getUser() {
    this._userService.getUser().subscribe()
  }
}
