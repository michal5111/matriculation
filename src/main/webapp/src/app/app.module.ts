import {BrowserModule} from '@angular/platform-browser';
import {LOCALE_ID, NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MaterialModule} from "./module/material/material.module";
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {ReactiveFormsModule} from "@angular/forms";
import {ApplicantSearchComponent} from "./component/applicant-search/applicant-search.component";
import {ApplicationsComponent} from "./component/applications/applications.component";
import {ApplicantComponent} from "./component/applicant/applicant.component";
import {HomeComponent} from './component/home/home.component';
import {PersonComponent} from './component/oracle/person/person.component';
import {PersonsComponent} from './component/oracle/persons/persons.component';
import {ImportSetupComponent} from './component/import/import-setup/import-setup.component';
import {ImportComponent} from './component/import/import/import.component';
import {ImportViewComponent} from './component/import/import-view/import-view.component';
import {UnauthorizedDialogComponent} from './component/dialog/unauthorized-dialog/unauthorized-dialog.component';
import {AuthInterceptor} from "./interceptors/auth-interceptor";
import {ForbiddenDialogComponent} from './component/dialog/forbidden-dialog/forbidden-dialog.component';
import {APP_BASE_HREF, PlatformLocation, registerLocaleData} from '@angular/common';
import localePl from '@angular/common/locales/pl';
import {MatPaginatorIntl} from "@angular/material/paginator";
import {MatPaginatorIntlPl} from "./customProviders/mat-paginator-intl-pl";
import {UpdateIndexNumberDialogComponent} from './component/dialog/update-index-number-dialog/update-index-number-dialog.component';

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
    ImportViewComponent,
    UnauthorizedDialogComponent,
    ForbiddenDialogComponent,
    UpdateIndexNumberDialogComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    HttpClientModule,
    ReactiveFormsModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    {provide: LOCALE_ID, useValue: 'pl-PL'},
    {provide: MatPaginatorIntl, useClass: MatPaginatorIntlPl},
    {
      provide: APP_BASE_HREF,
      useFactory: (s: PlatformLocation) => s.getBaseHrefFromDOM(),
      deps: [PlatformLocation]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor() {
    registerLocaleData(localePl, 'pl-PL');
  }
}
