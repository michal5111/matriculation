import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ImportService} from '../../../service/import-service/import.service';
import {SelectPersonDialogData} from '../../../model/dialog/select-person-dialog-data';
import {Person} from '../../../model/oracle/Person';
import {MatTableDataSource} from '@angular/material/table';
import {Selectable} from '../../../generics/selectable';
import {Applicant} from '../../../model/applications/applicant';
import {MatCheckboxChange} from '@angular/material/checkbox';

@Component({
  selector: 'app-select-person-dialog',
  templateUrl: './select-person-dialog.component.html',
  styleUrls: ['./select-person-dialog.component.sass']
})
export class SelectPersonDialogComponent implements OnInit {

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

  constructor(
    public dialogRef: MatDialogRef<SelectPersonDialogComponent>,
    private importService: ImportService,
    @Inject(MAT_DIALOG_DATA) public data: SelectPersonDialogData
  ) {
  }

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
      return person.isSelected === true;
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
