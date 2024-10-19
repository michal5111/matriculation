import {Routes} from '@angular/router';


import {UserService} from './service/user-service/user.service';


export const routes: Routes = [
  {path: '', loadComponent: () => import('./component/home/home.component').then(m => m.HomeComponent)},
  {
    path: 'import',
    loadComponent: () =>
      import('./component/import/import/import.component').then(m => m.ImportComponent),
    canActivate: [UserService],
    data: {
      authorities: ['ROLE_IMPORT_VIEW', 'ROLE_ADMIN']
    }
  },
  {
    path: 'userManager',
    loadComponent: () => import('./component/user-manager/user-manager.component').then(m => m.UserManagerComponent),
    canActivate: [UserService],
    data: {
      authorities: ['ROLE_ADMIN']
    }
  },
  {
    path: 'import/:id',
    loadComponent: () => import('./component/import/import-view/import-view.component').then(m => m.ImportViewComponent),
    canActivate: [UserService],
    data: {
      authorities: ['ROLE_IMPORT_VIEW', 'ROLE_ADMIN']
    }
  },
  {
    path: 'applications',
    loadComponent: () => import('./component/application-list/application-list.component').then(m => m.ApplicationListComponent),
    canActivate: [UserService],
    data: {
      authorities: ['ROLE_IMPORT_VIEW', 'ROLE_ADMIN']
    }
  }
];
