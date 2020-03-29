import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Import} from "../../../model/import/import";
import {ImportService} from "../../../service/import-service/import.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {tap} from "rxjs/operators";
import {MatSelectChange} from "@angular/material/select";
import {MatOptionSelectionChange} from "@angular/material/core";
import {Observable} from "rxjs";

@Component({
  selector: 'app-import-setup',
  templateUrl: './import-setup.component.html',
  styleUrls: ['./import-setup.component.sass']
})
export class ImportSetupComponent implements OnInit {

  import: Import = new Import();
  $availableRegistrationsObservable: Observable<[String]> = this.importService.getAvailableRegistrations();
  registrationProgrammes: [string];
  $indexPoolsObservable: Observable<[String]> = this.importService.getAvailableIndexPools();
  stages: [string];
  didacticCycles: [string];
  importCreationFormGroup: FormGroup;
  didacticCycleInputValue: String = '';

  constructor(private importService: ImportService, private formBuilder: FormBuilder) { }

  @Output() importCreatedEvent = new EventEmitter<string>();

  ngOnInit(): void {
    this.importCreationFormGroup = this.formBuilder.group({
      registration: ['', Validators.required],
      registrationProgramme: ['', Validators.required],
      indexPool: ['', Validators.required],
      stage: ['', Validators.required],
      didacticCycle: ['', Validators.required],
      startDate: ['', Validators.required],
      dateOfAddmision: ['', Validators.required]
    });

    // this.importService.getAvailableRegistrations().pipe(
    //   tap(results => this.registrations = results)
    // ).subscribe();
    // this.importService.getAvailableIndexPools().pipe(
    //   tap(results => this.indexPools = results)
    // ).subscribe();
  }

  onRegistrationSelectionChange(event: MatOptionSelectionChange, registration: String) {
    this.importService.getAvailableRegistrationProgrammes(registration).pipe(
      tap(results => this.registrationProgrammes = results)
    ).subscribe()
  }

  onRegistrationProgrammeChange(event: MatOptionSelectionChange, programmeCode: String) {
    this.importService.getAvailableStages(programmeCode).pipe(
      tap(results => this.stages = results)
    ).subscribe()
  }

  onDidacticCycleInputChange() {
    this.importService.findDidacticCycleCodes(this.didacticCycleInputValue).pipe(
      tap(results => this.didacticCycles = results)
    ).subscribe()
  }

  onSubmit() {
    this.import.registration = this.importCreationFormGroup.value.registration;
    this.import.programmeCode = this.importCreationFormGroup.value.registrationProgramme;
    this.import.didacticCycleCode = this.importCreationFormGroup.value.didacticCycle;
    this.import.dateOfAddmision = this.importCreationFormGroup.value.dateOfAddmision;
    this.import.startDate = this.importCreationFormGroup.value.startDate;
    this.import.indexPoolCode = this.importCreationFormGroup.value.indexPool;
    this.import.stageCode = this.importCreationFormGroup.value.stage;
    this.importService.createImport(this.import).subscribe(importObject => this.import = importObject);
    this.importCreatedEvent.next("importCreated")
  }
}
