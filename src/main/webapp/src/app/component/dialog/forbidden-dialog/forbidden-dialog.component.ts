import {Component, OnInit} from '@angular/core';
import {MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {CdkScrollable} from '@angular/cdk/scrolling';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-forbidden-dialog',
  templateUrl: './forbidden-dialog.component.html',
  styleUrls: ['./forbidden-dialog.component.sass'],
  standalone: true,
  imports: [MatDialogTitle, CdkScrollable, MatDialogContent, MatDialogActions, MatButton]
})
export class ForbiddenDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<ForbiddenDialogComponent>) {
  }

  ngOnInit(): void {
  }

  onClick() {
    this.dialogRef.close();
  }
}
