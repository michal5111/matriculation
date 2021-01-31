import {ErrorHandler, Injectable, NgZone} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {HttpErrorResponse} from '@angular/common/http';
import {ErrorDialogComponent} from '../component/dialog/error-dialog/error-dialog.component';
import {ErrorDialogData} from '../model/dialog/error-dialog-data';

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
  stacktrace: string;

  handleError(error: any): void {
    this.title = '';
    if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
      return;
    }
    console.log(error.constructor.name);
    console.log(error);
    if (error instanceof Error) {
      console.log(`Error: ${error}`);
      this.message = error.message;
      this.title += ` ${error.name}`;
      this.stacktrace = error.stack;
    }
    if (error instanceof HttpErrorResponse) {
      console.log(`Error: ${JSON.stringify(error.error)}`);
      this.message = error.error.message;
      if (error.error.status) {
        this.title += `${error.error.status}`;
      }
      this.title += ` ${error.error.error}`;
      this.stacktrace = error.error.path;
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
      data: new ErrorDialogData(this.title, this.message, this.stacktrace)
    });
    this.openedDialogs++;
    dialogRef.afterClosed().subscribe(
      () => {
        this.openedDialogs--;
      }
    );
  }
}
