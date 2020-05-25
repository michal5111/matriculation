import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UpdateIndexNumberDialogData} from '../../../model/dialog/update-index-number-dialog-data';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {PersonService} from '../../../service/person-service/person.service';

@Component({
  selector: 'app-update-index-number-dialog',
  templateUrl: './update-index-number-dialog.component.html',
  styleUrls: ['./update-index-number-dialog.component.sass']
})
export class UpdateIndexNumberDialogComponent implements OnInit {

  updateIndexNumberFormGroup: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<UpdateIndexNumberDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UpdateIndexNumberDialogData,
    private formBuilder: FormBuilder,
    private personService: PersonService
  ) {
  }

  sqlError: string;

  ngOnInit(): void {
    this.updateIndexNumberFormGroup = this.formBuilder.group({
      indexNumber: ['', Validators.required],
    });
  }

  onSubmit() {
    const newIndexNumber = this.updateIndexNumberFormGroup.value.indexNumber;
    this.personService.updateIndexNumberByUsosIdAndIndexType(
      this.data.personId,
      this.data.indexTypeCode,
      newIndexNumber
    ).subscribe(() => {
      this.dialogRef.close(newIndexNumber);
    }, error => {
      this.updateIndexNumberFormGroup.controls.indexNumber.setErrors({sqlError: true});
      this.sqlError = error.error.message;
    });
  }

  getErrorMessage() {
    if (this.updateIndexNumberFormGroup.controls.indexNumber.hasError('sqlError')) {
      return this.sqlError;
    }
  }
}
