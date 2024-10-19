import {Routes} from '@angular/router';
import {HomeComponent} from './component/home/home.component';
import {ImportComponent} from './component/import/import/import.component';
import {ImportViewComponent} from './component/import/import-view/import-view.component';
import {UserManagerComponent} from './component/user-manager/user-manager.component';
import {UserService} from './service/user-service/user.service';
import {ApplicationListComponent} from './component/application-list/application-list.component';

export const routes: Routes = [
  {path: '', component: HomeComponent},
  {
    path: 'import', component: ImportComponent, canActivate: [UserService], data: {
      authorities: ['ROLE_IMPORT_VIEW', 'ROLE_ADMIN']
    }
  },
  {
    path: 'userManager', component: UserManagerComponent, canActivate: [UserService], data: {
      authorities: ['ROLE_ADMIN']
    }
  },
  {
    path: 'import/:id', component: ImportViewComponent, canActivate: [UserService], data: {
      authorities: ['ROLE_IMPORT_VIEW', 'ROLE_ADMIN']
    }
  },
  {
    path: 'applications', component: ApplicationListComponent, canActivate: [UserService], data: {
      authorities: ['ROLE_IMPORT_VIEW', 'ROLE_ADMIN']
    }
  }
];
