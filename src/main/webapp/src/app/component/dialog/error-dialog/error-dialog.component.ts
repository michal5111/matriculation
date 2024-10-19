import {Component, Inject, OnInit} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef
} from '@angular/material/dialog';
import {ErrorDialogData} from '../../../model/dialog/error-dialog-data';
import {MatIcon} from '@angular/material/icon';
import {NgIf} from '@angular/common';
import {CdkScrollable} from '@angular/cdk/scrolling';
import {MatDivider} from '@angular/material/divider';
import {MatExpansionPanel, MatExpansionPanelContent, MatExpansionPanelHeader} from '@angular/material/expansion';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-error-dialog',
  templateUrl: './error-dialog.component.html',
  styleUrls: ['./error-dialog.component.sass'],
  standalone: true,
  imports: [MatIcon, NgIf, CdkScrollable, MatDialogContent, MatDivider, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelContent, MatDialogActions, MatButton, MatDialogClose]
})
export class ErrorDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ErrorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ErrorDialogData
  ) {
  }

  ngOnInit(): void {
  }
}
