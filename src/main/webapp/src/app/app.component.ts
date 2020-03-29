import { Component } from '@angular/core';
import { UserService } from "./service/user-service/user.service";
import {User} from "./model/user/user";
import {AppSettings} from "./app-settings";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {
  title = 'angularmatriculation';
  user: User;
  isAuthenticated = false;
  appHome = AppSettings.APP_HOME;

  constructor(private userService: UserService) {
    this.getUser()
  }

  getUser() {
    this.userService.getUser().subscribe((user: User) => {
      this.user = user;
      this.isAuthenticated = !!user.casAssertion;
    })
  }
}
