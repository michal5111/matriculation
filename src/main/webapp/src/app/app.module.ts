import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MaterialModule} from "./module/material/material.module";
import {HttpClientModule} from '@angular/common/http';
import {ReactiveFormsModule} from "@angular/forms";
import {ApplicantSearchComponent} from "./component/applicant-search/applicant-search.component";
import {ApplicationsComponent} from "./component/applications/applications.component";
import {ApplicantComponent} from "./component/applicant/applicant.component";
import {HomeComponent} from './component/home/home.component';
import {MatSortModule} from "@angular/material/sort";
import {PersonComponent} from './component/oracle/person/person.component';
import {PersonsComponent} from './component/oracle/persons/persons.component';
import {ImportSetupComponent} from './component/import/import-setup/import-setup.component';
import {ImportComponent} from './component/import/import/import.component';
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatIconModule} from "@angular/material/icon";
import {MatDatepickerModule} from "@angular/material/datepicker";
import { ImportViewComponent } from './component/import/import-view/import-view.component';

@NgModule({
  declarations: [
    AppComponent,
    ApplicantSearchComponent,
    ApplicantComponent,
    ApplicationsComponent,
    HomeComponent,
    PersonComponent,
    PersonsComponent,
    ImportSetupComponent,
    ImportComponent,
    ImportViewComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    HttpClientModule,
    ReactiveFormsModule,
    MatSortModule,
    MatAutocompleteModule,
    MatIconModule,
    MatDatepickerModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
