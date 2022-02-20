import {ErrorHandler, Injectable, NgZone} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {HttpErrorResponse} from '@angular/common/http';
import {ErrorDialogComponent} from '../component/dialog/error-dialog/error-dialog.component';
import {ErrorDialogData} from '../model/dialog/error-dialog-data';
import {BackendError} from './backend-error';

@Injectable()
export class ExceptionHandler implements ErrorHandler {

  constructor(
    private dialog: MatDialog,
    private ngZone: NgZone
  ) {
  }

  openedDialogs = 0;
  title: string;
  message: string;
  path: string;
  stacktrace: string;

  handleError(error: any): void {
    this.title = 'Error ';
    if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
      return;
    }
    console.log(error.constructor.name);
    console.log(error);
    if (error instanceof Error) {
      this.message = error.message;
      this.title += ` ${error.name}`;
      this.stacktrace = error.stack;
    }
    if (error instanceof HttpErrorResponse) {
      if (error.error instanceof BackendError) {
        const backedError = error.error;
        this.message = backedError.message;
        if (backedError.status) {
          this.title += backedError.status;
        }
        this.title += ` ${backedError.error}`;
        this.path = backedError.path;
        this.stacktrace = backedError.trace;
      } else {
        this.title += error.statusText;
        this.message = error.message;
        this.path = error.url;
      }
    }
    if (typeof error === 'string') {
      this.message = error;
    }
    if (this.openedDialogs > 2) {
      return;
    }
    this.ngZone.run(() => {
      this.openDialog();
    });
  }

  openDialog() {
    const dialogRef = this.dialog.open(ErrorDialogComponent, {
      data: new ErrorDialogData(this.title, this.message, this.stacktrace, this.path)
    });
    this.openedDialogs++;
    dialogRef.afterClosed().subscribe(
      () => {
        this.openedDialogs--;
      }
    );
  }
}
