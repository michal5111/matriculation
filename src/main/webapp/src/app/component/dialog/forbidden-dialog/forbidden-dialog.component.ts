import {Component, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-forbidden-dialog',
  templateUrl: './forbidden-dialog.component.html',
  styleUrls: ['./forbidden-dialog.component.sass']
})
export class ForbiddenDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<ForbiddenDialogComponent>) {
  }

  ngOnInit(): void {
  }

  onClick(event) {
    this.dialogRef.close();
  }
}
