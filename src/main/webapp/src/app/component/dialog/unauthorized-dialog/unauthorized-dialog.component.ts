import {Component, Inject, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {APP_BASE_HREF, Location} from '@angular/common';
import {CdkScrollable} from '@angular/cdk/scrolling';
import {MatAnchor} from '@angular/material/button';

@Component({
  selector: 'app-unauthorized-dialog',
  templateUrl: './unauthorized-dialog.component.html',
  styleUrls: ['./unauthorized-dialog.component.sass'],
  standalone: true,
  imports: [MatDialogTitle, CdkScrollable, MatDialogContent, MatDialogActions, MatAnchor]
})
export class UnauthorizedDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<UnauthorizedDialogComponent>,
    public router: Router,
    @Inject(APP_BASE_HREF) public baseHref: string, public location: Location
  ) {
  }

  ngOnInit(): void {
  }

}
