import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApplicantService } from '../../service/applicant-service/applicant.service'
import { Applicant } from '../../model/irk/applicant'
import { map, tap, flatMap } from 'rxjs/operators';
import {MatTableDataSource} from "@angular/material/table";

@Component({
  selector: 'app-applicant-search',
  templateUrl: './applicant-search.component.html',
  styleUrls: ['./applicant-search.component.sass']
})
export class ApplicantSearchComponent implements OnInit {

  selectSearchMethodFormGroup: FormGroup;
  inputDataFormGroup: FormGroup;
  applicantFromGroup: FormGroup;
  dataSource = new MatTableDataSource<Applicant>();
  displayedColumns: string[] = ['name', 'surname', 'email'];

  constructor(private formBuilder: FormBuilder, private applicantService: ApplicantService) { }

  ngOnInit() {
    this.selectSearchMethodFormGroup = this.formBuilder.group({
      searchMethod: ['', Validators.required]
    });
    this.inputDataFormGroup = this.formBuilder.group({
      input: ['', Validators.required]
    });
    this.applicantFromGroup = this.formBuilder.group({
      applicant: ['', Validators.required]
    })
  }

  inputDataOnSubmit() {
    this.applicantService.getApplicantByValues(
      this.selectSearchMethodFormGroup.value.searchMethod, this.inputDataFormGroup.value.input
    ).pipe(
      map(page => page.results),
      tap(results => this.dataSource.data = results),
      flatMap(applicants => applicants),
      map(applicant => applicant.image$ = this.applicantService.getPhoto(applicant.photo)),
    ).subscribe()
  }
}
