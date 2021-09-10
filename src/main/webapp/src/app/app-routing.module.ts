import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ApplicantSearchComponent} from './component/applicant-search/applicant-search.component';
import {ApplicantComponent} from './component/applicant/applicant.component';
import {ApplicationsComponent} from './component/applications/applications.component';
import {HomeComponent} from './component/home/home.component';
import {PersonComponent} from './component/oracle/person/person.component';
import {PersonsComponent} from './component/oracle/persons/persons.component';
import {ImportComponent} from './component/import/import/import.component';
import {ImportViewComponent} from './component/import/import-view/import-view.component';
import {UserManagerComponent} from './component/user-manager/user-manager.component';
import {UserService} from './service/user-service/user.service';

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'applicantsearch', component: ApplicantSearchComponent, canActivate: [UserService]},
  {path: 'applicant/:id', component: ApplicantComponent, canActivate: [UserService]},
  {path: 'applications', component: ApplicationsComponent, canActivate: [UserService]},
  {path: 'person/:id', component: PersonComponent, canActivate: [UserService]},
  {path: 'persons', component: PersonsComponent, canActivate: [UserService]},
  {path: 'import', component: ImportComponent, canActivate: [UserService]},
  {
    path: 'userManager', component: UserManagerComponent, canActivate: [UserService], data: {
      authorities: ['ROLE_ADMIN']
    }
  },
  {path: 'import/:id', component: ImportViewComponent, canActivate: [UserService]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
