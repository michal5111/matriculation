import { Component, OnInit } from '@angular/core';
import { Applicant } from '../../model/irk/applicant'
import { ApplicantService } from '../../service/applicant-service/applicant.service';
import { ActivatedRoute } from '@angular/router'

@Component({
  selector: 'app-applicant',
  templateUrl: './applicant.component.html',
  styleUrls: ['./applicant.component.sass']
})
export class ApplicantComponent implements OnInit {

  id: string;
  applicant: Applicant;

  constructor(public applicantService: ApplicantService, private route: ActivatedRoute) { }

  ngOnInit() {
    this.id = this.route.snapshot.params.id;
    if (!this.id) {
      return
    }
    this.applicantService.getApplicantById(this.id).subscribe(applicant => {
      this.applicant = applicant;
    })
  }

}
