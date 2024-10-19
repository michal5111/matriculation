import {APP_INITIALIZER, enableProdMode, ErrorHandler, importProvidersFrom, LOCALE_ID} from '@angular/core';

import {environment} from './environments/environment';
import {HTTP_INTERCEPTORS, HttpClient, provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {AuthInterceptor} from './app/interceptors/auth-interceptor';
import {MatPaginatorIntl} from '@angular/material/paginator';
import {MatPaginatorIntlPl} from './app/customProviders/mat-paginator-intl-pl';
import {APP_BASE_HREF, NgOptimizedImage, PlatformLocation} from '@angular/common';
import {ExceptionHandler} from './app/exceptionHandler/exception-handler';
import {WS_URL} from './app/injectableTokens/WS_URL';
import {InjectableRxStompConfig, RxStompService, rxStompServiceFactory} from '@stomp/ng2-stompjs';
import {MyRxStompConfig} from './app/configConsts/my-rx-stomp-config';
import {MAT_DATE_FORMATS} from '@angular/material/core';
import {MY_DATE_FORMATS} from './app/configConsts/MY_DATE_FORMATS';
import {UserService} from './app/service/user-service/user.service';
import {firstValueFrom} from 'rxjs';
import {bootstrapApplication, BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app/app-routing.module';
import {provideAnimations} from '@angular/platform-browser/animations';
import {ReactiveFormsModule} from '@angular/forms';
import {AppComponent} from './app/app.component';

function initUser(userService: UserService) {
  return (): Promise<any> => firstValueFrom(userService.init$());
}

function getCsrfToken(http: HttpClient) {
  return (): Promise<any> => firstValueFrom(http.get('/api/csrf'));
}


if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(BrowserModule, AppRoutingModule, ReactiveFormsModule, NgOptimizedImage),
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
        (s.protocol === 'http:' ? 'ws://' : 'wss://') + s.hostname + ':' + (s.port === '4200' ? '8081' : s.port)
        + baseHref + 'ws',
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
    },
    {
      provide: APP_INITIALIZER,
      useFactory: initUser,
      deps: [UserService],
      multi: true
    },
    {
      provide: APP_INITIALIZER,
      useFactory: getCsrfToken,
      deps: [HttpClient],
      multi: true
    },
    provideHttpClient(withInterceptorsFromDi()),
    provideAnimations()
  ]
})
  .catch(err => console.error(err));
