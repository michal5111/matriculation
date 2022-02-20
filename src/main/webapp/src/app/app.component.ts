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
    private location: Location
  ) {
    this.getUser();
  }

  getUser() {
    this.userService.getUser().subscribe();
  }

  getUserCaption() {
    const givenName = this.userService.user?.casAssertion?.principal?.attributes?.givenname;
    const surname = this.userService.user?.casAssertion?.principal?.attributes?.lastname;
    return `${givenName} ${surname}`;
  }

  getServiceUrl() {
    return this.baseHref + this.location.path().slice(1, this.location.path().length);
  }
}
