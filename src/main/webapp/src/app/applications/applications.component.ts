import { Component, OnInit } from '@angular/core';
import { ApplicationsService } from '../applications.service';
import { ApplicantService } from '../applicant.service'
import { Page } from '../page';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-applications',
  templateUrl: './applications.component.html',
  styleUrls: ['./applications.component.sass']
})
export class ApplicationsComponent implements OnInit {

  filterFormGroup: FormGroup

  constructor(
    private applicationsService: ApplicationsService,
    private applicantService: ApplicantService,
    private formBuilder: FormBuilder
    ) { }

  page: Page

  ngOnInit() {
    this.filterFormGroup = this.formBuilder.group({
      idInput: [''],
      qualifiedCheckBox: [''],
      admittedCheckBox: [''],
      paidCheckBox: [''],
      programmeInput: [''],
      registrationInput: ['']
    })
    this.getPage()
  }

  getPage() {
    this.applicationsService.getPage().subscribe(page => {
      this.page = page
      page.results.forEach(application => {
        application.applicant$ = this.applicantService.getApplicantById(application.user)
      })
    })
  }

}
