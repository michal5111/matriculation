import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {UnauthorizedDialogComponent} from '../component/dialog/unauthorized-dialog/unauthorized-dialog.component';
import {UserService} from '../service/user-service/user.service';
import {ForbiddenDialogComponent} from '../component/dialog/forbidden-dialog/forbidden-dialog.component';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private dialog: MatDialog, private userService: UserService) {
  }


  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(tap(() => {
      },
      (err: any) => {
        if (this.dialog.openDialogs.length > 0) {
          return;
        }
        if (err instanceof HttpErrorResponse) {
          if (err.status !== 401 && err.status !== 403) {
            return;
          }
          if (err.status === 401) {
            const dialogRef = this.dialog.open(UnauthorizedDialogComponent, {
              width: '250px'
            });
            this.userService.isAuthenticated = false;
          }
          if (err.status === 403) {
            const dialogRef = this.dialog.open(ForbiddenDialogComponent, {
              width: '250px'
            });
          }
        }
      }));
  }
}
