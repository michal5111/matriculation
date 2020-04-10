import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-unauthorized-dialog',
  templateUrl: './unauthorized-dialog.component.html',
  styleUrls: ['./unauthorized-dialog.component.sass']
})
export class UnauthorizedDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<UnauthorizedDialogComponent>,
    public _router: Router
  ) { }

  ngOnInit(): void {
  }

}
