import {APP_INITIALIZER, enableProdMode, ErrorHandler, importProvidersFrom, LOCALE_ID} from '@angular/core';

import {environment} from './environments/environment';
import {HTTP_INTERCEPTORS, HttpClient, provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {AuthInterceptor} from './app/interceptors/auth-interceptor';
import {MatPaginatorIntl} from '@angular/material/paginator';
import {MatPaginatorIntlPl} from './app/customProviders/mat-paginator-intl-pl';
import {APP_BASE_HREF, NgOptimizedImage, PlatformLocation, registerLocaleData} from '@angular/common';
import {ExceptionHandler} from './app/exceptionHandler/exception-handler';
import {WS_URL} from './app/injectableTokens/WS_URL';
import {InjectableRxStompConfig, RxStompService, rxStompServiceFactory} from '@stomp/ng2-stompjs';
import {MyRxStompConfig} from './app/configConsts/my-rx-stomp-config';
import {MAT_DATE_FORMATS, provideNativeDateAdapter} from '@angular/material/core';
import {MY_DATE_FORMATS} from './app/configConsts/MY_DATE_FORMATS';
import {UserService} from './app/service/user-service/user.service';
import {firstValueFrom} from 'rxjs';
import {bootstrapApplication, BrowserModule} from '@angular/platform-browser';
import {provideAnimations} from '@angular/platform-browser/animations';
import {ReactiveFormsModule} from '@angular/forms';
import {AppComponent} from './app/app.component';
import {provideRouter, withComponentInputBinding} from '@angular/router';
import {routes} from './app/routes';
import localePl from '@angular/common/locales/pl';

function initUser(userService: UserService) {
  return (): Promise<any> => firstValueFrom(userService.init$());
}

function getCsrfToken(http: HttpClient) {
  return (): Promise<any> => firstValueFrom(http.get('/api/csrf'));
}

registerLocaleData(localePl, 'pl-PL');


if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(BrowserModule, ReactiveFormsModule, NgOptimizedImage),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
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
    {provide: LOCALE_ID, useValue: 'pl-PL'},
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
    provideAnimations(),
    provideRouter(routes, withComponentInputBinding()),
    provideNativeDateAdapter()
  ]
})
  .catch(err => console.error(err));
