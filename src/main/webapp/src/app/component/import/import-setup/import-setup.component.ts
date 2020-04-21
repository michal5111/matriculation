import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {Import} from "../../../model/import/import";
import {ImportService} from "../../../service/import-service/import.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MatOptionSelectionChange} from "@angular/material/core";
import {Observable, Subscription} from "rxjs";
import {HttpErrorResponse} from "@angular/common/http";
import {filter, flatMap, tap} from "rxjs/operators";
import {IndexType} from "../../../model/oracle/index-type";
import {Registration} from "../../../model/irk/registration";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-import-setup',
  templateUrl: './import-setup.component.html',
  styleUrls: ['./import-setup.component.sass']
})
export class ImportSetupComponent implements OnInit, OnDestroy {

  import: Import = new Import();
  $availableRegistrationsObservable: Observable<[Registration]> = this.importService.getAvailableRegistrations();
  registrationProgrammes: [string];
  $indexPoolsObservable: Observable<[IndexType]> = this.importService.getAvailableIndexPools();
  stages: [string];
  didacticCycles: [string];
  importCreationFormGroup: FormGroup;
  changesSubscription: Subscription
  debounceTime = 400;

  constructor(private importService: ImportService, private formBuilder: FormBuilder, private snackBar: MatSnackBar) {
  }

  @Output() onImportCreatedEventEmitter = new EventEmitter<Import>();

  ngOnInit(): void {
    this.importCreationFormGroup = this.formBuilder.group({
      registration: ['', Validators.required],
      registrationProgramme: ['', Validators.required],
      indexPoolCode: ['', Validators.required],
      stage: ['', Validators.required],
      didacticCycle: ['', Validators.required],
      startDate: ['', Validators.required],
      dateOfAddmision: ['', Validators.required]
    });
    this.onDidacticCycleInputChanges()
    // this.importService.getAvailableRegistrations().pipe(
    //   tap(results => this.registrations = results)
    // ).subscribe();
    // this.importService.getAvailableIndexPools().pipe(
    //   tap(results => this.indexPools = results)
    // ).subscribe();
  }

  onRegistrationSelectionChange(event: MatOptionSelectionChange, registration: string) {
    this.importService.getAvailableRegistrationProgrammes(registration).pipe(
      tap(results => this.registrationProgrammes = results)
    ).subscribe(() => this.importCreationFormGroup.value.registrationProgramme = '')
  }

  onRegistrationProgrammeChange(event: MatOptionSelectionChange, programmeCode: string) {
    this.importService.getAvailableStages(programmeCode).pipe(
      tap(results => this.stages = results)
    ).subscribe( () => this.importCreationFormGroup.value.stage = '' )
  }

  onDidacticCycleInputChanges() {
    this.changesSubscription = this.importCreationFormGroup.get('didacticCycle').valueChanges.pipe(
      filter(value => value !== undefined && value !== '' && value !== null && value.length >= 2),
      flatMap(value => this.importService.findDidacticCycleCodes(value)),
      tap(didacticCycles => this.didacticCycles = didacticCycles)
    ).subscribe()
  }

  onSubmit() {
    this.import.registration = this.importCreationFormGroup.value.registration;
    this.import.programmeCode = this.importCreationFormGroup.value.registrationProgramme;
    this.import.didacticCycleCode = this.importCreationFormGroup.value.didacticCycle;
    this.import.dateOfAddmision = this.importCreationFormGroup.value.dateOfAddmision;
    this.import.startDate = this.importCreationFormGroup.value.startDate;
    this.import.indexPoolCode = this.importCreationFormGroup.value.indexPoolCode;
    this.import.stageCode = this.importCreationFormGroup.value.stage;
    this.importService.createImport(this.import).subscribe(
      importObject => this.onImportCreated(importObject),
      error => this.onError(error)
    );
  }

  onError(error) {
    if (error instanceof HttpErrorResponse) {
      alert(`Błąd: ${error.error.message}`)
    }
  }

  onImportCreated(importObject: Import) {
    let snackBarRef = this.snackBar.open("Import utworzony", "OK", {
      duration: 3000
    })
    snackBarRef.onAction().subscribe(() => snackBarRef.dismiss())
    this.importCreationFormGroup.reset();
    this.import = importObject;
    this.onImportCreatedEventEmitter.next(this.import)
  }

  ngOnDestroy(): void {
    this.snackBar.ngOnDestroy()
    this.changesSubscription.unsubscribe()
  }
}
