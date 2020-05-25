import {Component, Inject, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {MatDialogRef} from '@angular/material/dialog';
import {APP_BASE_HREF, Location} from '@angular/common';

@Component({
  selector: 'app-unauthorized-dialog',
  templateUrl: './unauthorized-dialog.component.html',
  styleUrls: ['./unauthorized-dialog.component.sass']
})
export class UnauthorizedDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<UnauthorizedDialogComponent>,
    public _router: Router,
    @Inject(APP_BASE_HREF) public baseHref: string, public location: Location
  ) { }

  ngOnInit(): void {
  }

}
