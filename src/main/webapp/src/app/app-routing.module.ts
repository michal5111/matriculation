import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ApplicantSearchComponent } from './irk/applicant-search/applicant-search.component'
import { ApplicantComponent } from './irk/applicant/applicant.component';
import { ApplicationsComponent } from './irk/applications/applications.component';
import {HomeComponent} from "./home/home.component";
import {PersonComponent} from "./oracle/person/person.component";
import {PersonsComponent} from "./oracle/persons/persons.component";

const routes: Routes = [
  {path: 'applicantsearch', component: ApplicantSearchComponent },
  {path: 'applicant/:id', component: ApplicantComponent},
  {path: 'applications', component: ApplicationsComponent},
  {path: 'person/:id', component: PersonComponent},
  {path: 'persons', component: PersonsComponent},
  {path: '', component: HomeComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
