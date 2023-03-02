import {BrowserModule} from '@angular/platform-browser';
import {ErrorHandler, LOCALE_ID, NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MaterialModule} from './module/material/material.module';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {ReactiveFormsModule} from '@angular/forms';
import {HomeComponent} from './component/home/home.component';
import {ImportSetupComponent} from './component/import/import-setup/import-setup.component';
import {ImportComponent} from './component/import/import/import.component';
import {ImportViewComponent} from './component/import/import-view/import-view.component';
import {UnauthorizedDialogComponent} from './component/dialog/unauthorized-dialog/unauthorized-dialog.component';
import {AuthInterceptor} from './interceptors/auth-interceptor';
import {ForbiddenDialogComponent} from './component/dialog/forbidden-dialog/forbidden-dialog.component';
import {APP_BASE_HREF, PlatformLocation, registerLocaleData} from '@angular/common';
import localePl from '@angular/common/locales/pl';
import {MatPaginatorIntl} from '@angular/material/paginator';
import {MatPaginatorIntlPl} from './customProviders/mat-paginator-intl-pl';
import {
  UpdateIndexNumberDialogComponent
} from './component/dialog/update-index-number-dialog/update-index-number-dialog.component';
import {FooterComponent} from './component/footer/footer.component';
import {ConfirmationDialogComponent} from './component/dialog/confirmation-dialog/confirmation-dialog.component';
import {ErrorDialogComponent} from './component/dialog/error-dialog/error-dialog.component';
import {UserManagerComponent} from './component/user-manager/user-manager.component';
import {UserEditorComponent} from './component/dialog/user-editor/user-editor.component';
import {ProgressViewerComponent} from './component/progress-viewer/progress-viewer.component';
import {
  ImportStatusIndicatorComponent
} from './component/import/import-status-indicator/import-status-indicator.component';
import {AddUserDialogComponent} from './component/dialog/add-user-dialog/add-user-dialog.component';
import {ExceptionHandler} from './exceptionHandler/exception-handler';
import {SelectPersonDialogComponent} from './component/dialog/select-person-dialog/select-person-dialog.component';
import {MyRxStompConfig} from './configConsts/my-rx-stomp-config';
import {InjectableRxStompConfig, RxStompService, rxStompServiceFactory} from '@stomp/ng2-stompjs';
import {WS_URL} from './injectableTokens/WS_URL';
import {MAT_DATE_FORMATS} from '@angular/material/core';
import {MY_DATE_FORMATS} from './configConsts/MY_DATE_FORMATS';
import {DatesValidatorDirective} from './validator/dates-validator.directive';
import {ReactiveFileInputComponent} from './component/reactive-file-input/reactive-file-input.component';
import {ImportEditorComponent} from './component/dialog/import-editor/import-editor.component';
import {ApplicationListComponent} from './component/application-list/application-list.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    ImportSetupComponent,
    ImportComponent,
    ImportViewComponent,
    UnauthorizedDialogComponent,
    ForbiddenDialogComponent,
    UpdateIndexNumberDialogComponent,
    FooterComponent,
    ConfirmationDialogComponent,
    ErrorDialogComponent,
    UserManagerComponent,
    UserEditorComponent,
    ProgressViewerComponent,
    ImportStatusIndicatorComponent,
    AddUserDialogComponent,
    SelectPersonDialogComponent,
    DatesValidatorDirective,
    ReactiveFileInputComponent,
    ImportEditorComponent,
    ApplicationListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    HttpClientModule,
    ReactiveFormsModule
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
    },
    {
      provide: ErrorHandler,
      useClass: ExceptionHandler
    },
    {
      provide: WS_URL,
      useFactory: (s: PlatformLocation, baseHref: string) =>
        (s.protocol === 'http:' ? 'ws://' : 'wss://') + s.hostname + ':' + (s.port === '4200' ? '8081' : s.port) + baseHref + 'ws',
      deps: [PlatformLocation, APP_BASE_HREF]
    },
    {
      provide: InjectableRxStompConfig,
      useFactory: (wsUrl: string) => {
        MyRxStompConfig.brokerURL = wsUrl;
        return MyRxStompConfig;
      },
      deps: [WS_URL]
    },
    {
      provide: RxStompService,
      useFactory: rxStompServiceFactory,
      deps: [InjectableRxStompConfig]
    },
    {
      provide: MAT_DATE_FORMATS,
      useValue: MY_DATE_FORMATS
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor() {
    registerLocaleData(localePl, 'pl-PL');
  }
}
