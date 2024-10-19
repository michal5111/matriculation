import {Component, Inject} from '@angular/core';
import {UserService} from './service/user-service/user.service';
import {APP_BASE_HREF, AsyncPipe, Location, NgIf, NgOptimizedImage} from '@angular/common';
import {RouterLink, RouterOutlet} from '@angular/router';
import {FooterComponent} from './component/footer/footer.component';
import {MatAnchor} from '@angular/material/button';
import {MatToolbar} from '@angular/material/toolbar';
import {MatDrawerContainer} from '@angular/material/sidenav';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass'],
  standalone: true,
  imports: [
    RouterLink,
    NgOptimizedImage,
    NgIf,
    RouterOutlet,
    FooterComponent,
    AsyncPipe,
    MatAnchor,
    MatToolbar,
    MatDrawerContainer,
    MatIcon
  ]
})
export class AppComponent {
  title = 'matriculation';

  constructor(
    public userService: UserService,
    @Inject(APP_BASE_HREF) public baseHref: string,
    private location: Location
  ) {
  }

  getServiceUrl() {
    return this.baseHref + this.location.path().slice(1, this.location.path().length);
  }
}
