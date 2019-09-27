import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from "./material/material.module";
import { HttpClientModule } from '@angular/common/http';
import {ReactiveFormsModule} from "@angular/forms";
import {ApplicantSearchComponent} from "./irk/applicant-search/applicant-search.component";
import {ApplicationsComponent} from "./irk/applications/applications.component";
import {ApplicantComponent} from "./irk/applicant/applicant.component";
import { HomeComponent } from './home/home.component';
import {MatSortModule} from "@angular/material/sort";
import { PersonComponent } from './oracle/person/person.component';
import { PersonsComponent } from './oracle/persons/persons.component';

@NgModule({
  declarations: [
    AppComponent,
    ApplicantSearchComponent,
    ApplicantComponent,
    ApplicationsComponent,
    HomeComponent,
    PersonComponent,
    PersonsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    HttpClientModule,
    ReactiveFormsModule,
    MatSortModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
