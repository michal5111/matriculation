import {Component, inject, OnInit} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {ConfirmationDialogData} from '../../../model/dialog/confirmation-dialog-data';
import {CdkScrollable} from '@angular/cdk/scrolling';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.sass'],
  standalone: true,
  imports: [MatDialogTitle, CdkScrollable, MatDialogContent, MatDialogActions, MatButton]
})
export class ConfirmationDialogComponent implements OnInit {
  dialogRef = inject<MatDialogRef<ConfirmationDialogComponent>>(MatDialogRef);
  data = inject<ConfirmationDialogData>(MAT_DIALOG_DATA);


  ngOnInit(): void {
  }

}
