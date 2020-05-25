import {Component, Inject} from '@angular/core';
import {UserService} from './service/user-service/user.service';
import {APP_BASE_HREF, Location} from '@angular/common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {
  title = 'matriculation';

  constructor(public _userService: UserService, @Inject(APP_BASE_HREF) public baseHref: string, public location: Location) {
    this.getUser();
  }

  getUser() {
    this._userService.getUser().subscribe();
  }
}
