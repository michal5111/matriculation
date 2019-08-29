import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ApplicantSearchComponent } from './applicant-search/applicant-search.component'
import { ApplicantComponent } from './applicant/applicant.component';
import { ApplicationsComponent } from './applications/applications.component';
import {HomeComponent} from "./home/home.component";

const routes: Routes = [
  {path: 'applicantsearch', component: ApplicantSearchComponent },
  {path: 'applicant/:id', component: ApplicantComponent},
  {path: 'applications', component: ApplicationsComponent},
  {path: '', component: HomeComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
