import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Import} from '../../../model/import/import';
import {ImportSetupComponent} from '../../import/import-setup/import-setup.component';

@Component({
  selector: 'app-import-editor',
  templateUrl: './import-editor.component.html',
  styleUrls: ['./import-editor.component.sass'],
  standalone: true,
  imports: [ImportSetupComponent]
})
export class ImportEditorComponent implements OnInit {

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { import: Import | null } | null = null,
    public dialogRef: MatDialogRef<ImportEditorComponent>
  ) {
  }

  ngOnInit(): void {
  }

}
