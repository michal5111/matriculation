import {Component, inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef} from '@angular/material/dialog';
import {ImportService} from '../../../service/import-service/import.service';
import {SelectPersonDialogData} from '../../../model/dialog/select-person-dialog-data';
import {Person} from '../../../model/oracle/Person';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource
} from '@angular/material/table';
import {Selectable} from '../../../generics/selectable';
import {Applicant} from '../../../model/applications/applicant';
import {MatCheckbox, MatCheckboxChange} from '@angular/material/checkbox';
import {CdkScrollable} from '@angular/cdk/scrolling';
import {AsyncPipe, DatePipe} from '@angular/common';
import {MatDivider} from '@angular/material/divider';
import {MatButton} from '@angular/material/button';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';

@Component({
  selector: 'app-select-person-dialog',
  templateUrl: './select-person-dialog.component.html',
  styleUrls: ['./select-person-dialog.component.sass'],
  standalone: true,
  imports: [
    CdkScrollable,
    MatDialogContent,
    MatTable,
    MatColumnDef,
    MatHeaderCellDef,
    MatHeaderCell,
    MatCellDef,
    MatCell,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRowDef,
    MatRow,
    MatDivider,
    MatCheckbox,
    MatDialogActions,
    MatButton,
    DatePipe,
    AsyncPipe
  ]
})
export class SelectPersonDialogComponent {
  readonly dialogRef = inject<MatDialogRef<SelectPersonDialogComponent>>(MatDialogRef);
  private readonly importService = inject(ImportService);
  readonly data = inject<SelectPersonDialogData>(MAT_DIALOG_DATA);

  people$: Observable<Person[]> = this.importService.getPotentialDuplicates(this.data.application.applicant.id).pipe(
    tap(people => this.dataSource.data = people.map(person => new Selectable(person, false)))
  );
  applicant: Applicant = this.data.application.applicant;
  displayedColumns: string[] = [
    'checkbox',
    'id',
    'name',
    'middleName',
    'surname',
    'birthDate',
    'idNumber',
    'pesel',
    'sex',
    'email',
    'privateEmail'
  ];
  applicantDisplayedColumns: string[] = [
    'id',
    'name',
    'middleName',
    'surname',
    'birthDate',
    'pesel',
    'idNumber',
    'sex',
    'email'
  ];
  dataSource = new MatTableDataSource<Selectable<Person>>();
  applicantDataSource = new MatTableDataSource<Applicant>([this.applicant]);

  getSelected(): Selectable<Person>[] {
    return this.dataSource.data.filter(person => {
      return person.isSelected;
    });
  }

  selectPerson(person: Person) {
    this.dialogRef.close({
      person
    });
  }

  confirmNotDuplicate() {
    this.dialogRef.close({});
  }

  onCheckboxChange(event: MatCheckboxChange, selected: Selectable<Person>) {
    selected.isSelected = event.checked;
  }

}
