import {Injectable} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {MatDialog} from "@angular/material/dialog";
import {UnauthorizedDialogComponent} from "../component/dialog/unauthorized-dialog/unauthorized-dialog.component";
import {UserService} from "../service/user-service/user.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private dialog: MatDialog, private userService: UserService) {
  }


  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    // if (currentUser && currentUser.token) {
    //   request = request.clone({
    //     setHeaders: {
    //       'Content-Type': 'application/json',
    //       Authorization: `JWT ${currentUser.token}`
    //     }
    //   });
    // }

    return next.handle(request).pipe(tap(() => {
      },
      (err: any) => {
        if (err instanceof HttpErrorResponse) {
          if (err.status !== 401 && err.status !== 403) {
            return;
          }
          if (err.status === 401) {
            const dialogRef = this.dialog.open(UnauthorizedDialogComponent, {
              width: '250px'
            });
            this.userService.isAuthenticated = false
          }
          if (err.status === 403) {
            const dialogRef = this.dialog.open(UnauthorizedDialogComponent, {
              width: '250px'
            });
          }
        }
      }));
  }
}
