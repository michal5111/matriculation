import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ApplicantSearchComponent } from './applicant-search/applicant-search.component'
import { ApplicantComponent } from './applicant/applicant.component';
import { ApplicationsComponent } from './applications/applications.component';

const routes: Routes = [
  {path: 'applicantsearch', component: ApplicantSearchComponent },
  {path: 'applicant/:id', component: ApplicantComponent},
  {path: 'applications', component: ApplicationsComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
