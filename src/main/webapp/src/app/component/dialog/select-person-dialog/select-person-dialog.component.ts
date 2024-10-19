import { Component, OnInit, inject } from '@angular/core';
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
import {DatePipe} from '@angular/common';
import {MatDivider} from '@angular/material/divider';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-select-person-dialog',
  templateUrl: './select-person-dialog.component.html',
  styleUrls: ['./select-person-dialog.component.sass'],
  standalone: true,
  imports: [CdkScrollable, MatDialogContent, MatTable, MatColumnDef, MatHeaderCellDef, MatHeaderCell, MatCellDef, MatCell, MatHeaderRowDef, MatHeaderRow, MatRowDef, MatRow, MatDivider, MatCheckbox, MatDialogActions, MatButton, DatePipe]
})
export class SelectPersonDialogComponent implements OnInit {
  dialogRef = inject<MatDialogRef<SelectPersonDialogComponent>>(MatDialogRef);
  private importService = inject(ImportService);
  data = inject<SelectPersonDialogData>(MAT_DIALOG_DATA);


  persons: Person[] = [];
  applicant: Applicant | null = null;
  selected: Array<Selectable<Person>> = [];
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
    'idNumber',
    'sex',
    'email'
  ];
  dataSource = new MatTableDataSource<Selectable<Person>>();
  applicantDataSource = new MatTableDataSource<Applicant>();

  ngOnInit(): void {
    this.applicant = this.data.application.applicant;
    this.applicantDataSource.data = [this.applicant];
    this.importService.getPotentialDuplicates(this.data.application.applicant.id).subscribe(persons => {
      this.persons = persons;
      this.dataSource.data = persons.map(person => {
        return new Selectable(person, false);
      });
    });
  }

  getSelected(): Selectable<Person>[] {
    return this.dataSource.data.filter(person => {
      return person.isSelected;
    });
  }

  selectPerson(person: Person) {
    this.dialogRef.close({
      notDuplicate: false,
      person
    });
  }

  confirmNotDuplicate() {
    this.dialogRef.close({
      notDuplicate: true
    });
  }

  onCheckboxChange(event: MatCheckboxChange, selected: Selectable<Person>) {
    selected.isSelected = event.checked;
  }

}
