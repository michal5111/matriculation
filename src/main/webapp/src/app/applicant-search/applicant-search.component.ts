import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApplicantService } from '../applicant.service'
import { Applicant } from '../applicant/applicant'
import { map, tap, switchMap, flatMap } from 'rxjs/operators';

@Component({
  selector: 'app-applicant-search',
  templateUrl: './applicant-search.component.html',
  styleUrls: ['./applicant-search.component.sass']
})
export class ApplicantSearchComponent implements OnInit {

  selectSearchMethodFormGroup: FormGroup;
  inputDataFormGroup: FormGroup;
  applicantFromGroup: FormGroup;
  applicants: [Applicant];

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
      tap(results => this.applicants = results),
      tap(applicant => console.log(applicant)),
      flatMap(applicants => applicants),
      map(applicant => applicant.image$ = this.applicantService.getPhoto(applicant.photo))
    ).subscribe()
  }
}
