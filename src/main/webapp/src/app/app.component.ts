import {Component, Inject} from '@angular/core';
import {UserService} from './service/user-service/user.service';
import {APP_BASE_HREF, Location} from '@angular/common';
import {environment} from '../environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {
  title = 'matriculation';
  appEnvironment = environment;
  json = JSON;

  constructor(
    public userService: UserService,
    @Inject(APP_BASE_HREF) public baseHref: string,
    public location: Location
  ) {
    this.getUser();
  }

  getUser() {
    this.userService.getUser().subscribe();
  }
}
