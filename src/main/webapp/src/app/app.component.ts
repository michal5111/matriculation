import { Component, inject } from '@angular/core';
import {UserService} from './service/user-service/user.service';
import {APP_BASE_HREF, AsyncPipe, Location, NgOptimizedImage} from '@angular/common';
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
  userService = inject(UserService);
  baseHref = inject(APP_BASE_HREF);
  private location = inject(Location);

  title = 'matriculation';

  getServiceUrl() {
    return this.baseHref + this.location.path().slice(1, this.location.path().length);
  }
}
