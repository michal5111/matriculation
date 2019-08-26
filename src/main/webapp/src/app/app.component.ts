import { Component } from '@angular/core';
import { UserService } from "./user.service";
import {User} from "./user/user";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {
  title = 'angularmatriculation';
  user: User;
  isAuthenticated = false

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
