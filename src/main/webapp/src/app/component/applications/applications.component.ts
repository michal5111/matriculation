import {Component, OnInit} from '@angular/core';
import {ApplicationsService} from '../../service/application-service/applications.service';
import {ApplicantService} from '../../service/applicant-service/applicant.service';
import {Page} from '../../model/applications/page';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {MatTableDataSource} from '@angular/material/table';
import {Application} from '../../model/applications/application';

@Component({
  selector: 'app-applications',
  templateUrl: './applications.component.html',
  styleUrls: ['./applications.component.sass']
})
export class ApplicationsComponent implements OnInit {

  filterFormGroup: UntypedFormGroup;

  constructor(
    private applicationsService: ApplicationsService,
    private applicantService: ApplicantService,
    private formBuilder: UntypedFormBuilder
    ) { }

  page: Page<Application>;
  dataSource = new MatTableDataSource();

  ngOnInit() {
    this.filterFormGroup = this.formBuilder.group({
      idInput: [''],
      qualifiedCheckBox: [''],
      admittedCheckBox: [''],
      paidCheckBox: [''],
      programmeInput: [''],
      registrationInput: ['']
    });
    // this.getPage()
  }

  // getPage() {
  //   this.applicationsService.getPage().subscribe(page => {
  //     this.page = page;
  //     page.results.forEach(application => {
  //       application.applicant$ = this.applicantService.getApplicantById(application.user.toString())
  //     })
  //   })
  // }

}
