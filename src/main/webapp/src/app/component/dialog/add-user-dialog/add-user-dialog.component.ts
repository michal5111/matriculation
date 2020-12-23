import { Component, OnInit } from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-add-user-dialog',
  templateUrl: './add-user-dialog.component.html',
  styleUrls: ['./add-user-dialog.component.sass']
})
export class AddUserDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AddUserDialogComponent>
  ) { }

  ngOnInit(): void {
  }

}
