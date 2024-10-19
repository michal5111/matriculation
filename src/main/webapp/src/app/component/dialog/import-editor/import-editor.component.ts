import {Component, inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ImportSetupComponent} from '../../import/import-setup/import-setup.component';

@Component({
  selector: 'app-import-editor',
  templateUrl: './import-editor.component.html',
  styleUrls: ['./import-editor.component.sass'],
  standalone: true,
  imports: [ImportSetupComponent]
})
export class ImportEditorComponent implements OnInit {
  data = inject(MAT_DIALOG_DATA);
  dialogRef = inject<MatDialogRef<ImportEditorComponent>>(MatDialogRef);


  ngOnInit(): void {
  }

}
