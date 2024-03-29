import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UpdateIndexNumberDialogData} from '../../../model/dialog/update-index-number-dialog-data';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {UsosService} from '../../../service/usos-service/usos.service';

@Component({
  selector: 'app-update-index-number-dialog',
  templateUrl: './update-index-number-dialog.component.html',
  styleUrls: ['./update-index-number-dialog.component.sass']
})
export class UpdateIndexNumberDialogComponent implements OnInit {

  updateIndexNumberFormGroup: FormGroup<{ indexNumber: FormControl<string | null> }>;

  constructor(
    public dialogRef: MatDialogRef<UpdateIndexNumberDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UpdateIndexNumberDialogData,
    private usosService: UsosService
  ) {
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
