import {Component, inject, OnInit} from '@angular/core';
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
  dialogRef = inject<MatDialogRef<UnauthorizedDialogComponent>>(MatDialogRef);
  router = inject(Router);
  baseHref = inject(APP_BASE_HREF);
  location = inject(Location);


  ngOnInit(): void {
  }

}
