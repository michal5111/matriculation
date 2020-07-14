import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ErrorDialogData} from '../../../model/dialog/error-dialog-data';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-error-dialog',
  templateUrl: './error-dialog.component.html',
  styleUrls: ['./error-dialog.component.sass']
})
export class ErrorDialogComponent implements OnInit {

  title: string;
  message: string;
  stacktrace: string;

  constructor(
    public dialogRef: MatDialogRef<ErrorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ErrorDialogData,
  ) {
  }

  ngOnInit(): void {
    console.log(this.data.error.constructor.name);
    this.title = this.data.title;
    this.stacktrace = this.data.stacktrace;
    if (this.data.error instanceof Error) {
      console.log(`Error: ${this.data.error}`);
      this.message = this.data.error.message;
      this.title += ` ${this.data.error.name}`;
      this.data.stacktrace = this.data.error.stack;
    }
    if (this.data.error instanceof HttpErrorResponse) {
      console.log(`Error: ${JSON.stringify(this.data.error.error)}`);
      this.message = this.data.error.message;
      this.title += ` ${this.data.error.name}`;
      this.data.stacktrace = this.data.error.statusText;
    }
    if (typeof this.data.error === 'string') {
      this.message = this.data.error;
    }
  }

}
