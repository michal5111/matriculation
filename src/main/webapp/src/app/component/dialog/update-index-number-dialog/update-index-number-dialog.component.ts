import { Component, OnInit, inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {UpdateIndexNumberDialogData} from '../../../model/dialog/update-index-number-dialog-data';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {UsosService} from '../../../service/usos-service/usos.service';
import {CdkScrollable} from '@angular/cdk/scrolling';
import {MatError, MatFormField, MatLabel} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';

import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-update-index-number-dialog',
  templateUrl: './update-index-number-dialog.component.html',
  styleUrls: ['./update-index-number-dialog.component.sass'],
  standalone: true,
  imports: [MatDialogTitle, ReactiveFormsModule, CdkScrollable, MatDialogContent, MatFormField, MatLabel, MatInput, MatError, MatDialogActions, MatButton]
})
export class UpdateIndexNumberDialogComponent implements OnInit {
  dialogRef = inject<MatDialogRef<UpdateIndexNumberDialogComponent>>(MatDialogRef);
  data = inject<UpdateIndexNumberDialogData>(MAT_DIALOG_DATA);
  private usosService = inject(UsosService);


  updateIndexNumberFormGroup: FormGroup<{ indexNumber: FormControl<string | null> }>;

  constructor() {
    this.updateIndexNumberFormGroup = new FormGroup({
      indexNumber: new FormControl<string>('', Validators.required)
    });
  }

  sqlError: string | null = null;

  ngOnInit(): void {

  }

  onSubmit() {
    const newIndexNumber = this.updateIndexNumberFormGroup.value.indexNumber;
    this.usosService.updateIndexNumberByUsosIdAndIndexType(
      this.data.personId,
      this.data.indexTypeCode,
      newIndexNumber ?? ''
    ).subscribe({
      next: () => {
        this.dialogRef.close(newIndexNumber);
      },
      error: (error) => {
        this.updateIndexNumberFormGroup.controls.indexNumber.setErrors({sqlError: true});
        this.sqlError = error.error.message;
      }
    });
  }

  getErrorMessage() {
    if (this.updateIndexNumberFormGroup.controls.indexNumber.hasError('sqlError')) {
      return this.sqlError;
    }
    return 'ERROR';
  }
}
