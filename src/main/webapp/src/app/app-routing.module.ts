import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ApplicantSearchComponent } from './component/applicant-search/applicant-search.component'
import { ApplicantComponent } from './component/applicant/applicant.component';
import { ApplicationsComponent } from './component/applications/applications.component';
import {HomeComponent} from "./component/home/home.component";
import {PersonComponent} from "./component/oracle/person/person.component";
import {PersonsComponent} from "./component/oracle/persons/persons.component";
import {ImportComponent} from "./component/import/import/import.component";
import {ImportViewComponent} from "./component/import/import-view/import-view.component";

const routes: Routes = [
  {path: 'applicantsearch', component: ApplicantSearchComponent },
  {path: 'applicant/:id', component: ApplicantComponent},
  {path: 'applications', component: ApplicationsComponent},
  {path: 'person/:id', component: PersonComponent},
  {path: 'persons', component: PersonsComponent},
  {path: 'import', component: ImportComponent},
  {path: 'import/:id', component: ImportViewComponent},
  {path: '', component: HomeComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
