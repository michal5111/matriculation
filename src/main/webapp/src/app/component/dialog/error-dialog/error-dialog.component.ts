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

  message: string;

  constructor(
    public dialogRef: MatDialogRef<ErrorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ErrorDialogData,
  ) {
  }

  ngOnInit(): void {
    if (this.data.error instanceof HttpErrorResponse) {
      this.message = this.data.error.error.message;
    }
  }

}
