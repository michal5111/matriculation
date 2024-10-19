import {Component, inject, OnInit} from '@angular/core';
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
  dialogRef = inject<MatDialogRef<ForbiddenDialogComponent>>(MatDialogRef);


  ngOnInit(): void {
  }

  onClick() {
    this.dialogRef.close();
  }
}
