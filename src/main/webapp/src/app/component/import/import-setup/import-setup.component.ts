import {ChangeDetectorRef, Component, EventEmitter, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {Import} from '../../../model/import/import';
import {ImportService} from '../../../service/import-service/import.service';
import {FormBuilder, FormGroup, FormGroupDirective, Validators} from '@angular/forms';
import {Observable, Subscription} from 'rxjs';
import {filter, switchMap, tap} from 'rxjs/operators';
import {IndexType} from '../../../model/oracle/index-type';
import {Registration} from '../../../model/applications/registration';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatDialog} from '@angular/material/dialog';
import {MatSelectChange} from '@angular/material/select';
import {DataSource} from '../../../model/import/dataSource';
import {Programme} from '../../../model/applications/programme';
import {UsosService} from '../../../service/usos-service/usos.service';

@Component({
  selector: 'app-import-setup',
  templateUrl: './import-setup.component.html',
  styleUrls: ['./import-setup.component.sass']
})
export class ImportSetupComponent implements OnInit, OnDestroy {

  import: Import = new Import();
  dataSourceId = '';
  $availableDataSourcesObservable: Observable<[DataSource]> = this.importService.getAvailableDataSources();
  registrations: Registration[];
  registrationProgrammes: Programme[];
  $indexPoolsObservable: Observable<[IndexType]> = this.usosService.getAvailableIndexPools();
  stages: string[];
  didacticCycles: string[];
  importCreationFormGroup: FormGroup;
  changesSubscription: Subscription;

  constructor(
    private importService: ImportService,
    private usosService: UsosService,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private cd: ChangeDetectorRef
  ) {
  }

  @Output() importCreated = new EventEmitter<Import>();
  @ViewChild(FormGroupDirective) formGroupDirective: FormGroupDirective;

  ngOnInit(): void {
    this.importCreationFormGroup = this.formBuilder.group({
      dataSource: ['', Validators.required],
      registration: ['', Validators.required],
      registrationProgramme: ['', Validators.required],
      indexPoolCode: ['', Validators.required],
      stage: ['', Validators.required],
      didacticCycle: ['', Validators.required],
      startDate: ['', Validators.required],
      dateOfAddmision: ['', Validators.required],
      dataFile: ['']
    });
    this.onDidacticCycleInputChanges();
    this.importCreationFormGroup.controls.dataFile.valueChanges.subscribe((fileInput: any) => {
      const reader = new FileReader();

      if (fileInput && fileInput.files[0]) {
        const file = fileInput.files[0];
        reader.readAsDataURL(file);
        reader.onload = () => {
          this.import.dataFile = reader.result.toString().replace(/^data:(.*,)?/, '');
          this.cd.markForCheck();
        };
      }
    });
  }

  onRegistrationSelectionChange(event: MatSelectChange): void {
    this.importService.getAvailableRegistrationProgrammes(event.value, this.dataSourceId).pipe(
      tap(results => this.registrationProgrammes = results)
    ).subscribe(
      () => {
        this.importCreationFormGroup.patchValue({registrationProgramme: null, stage: null});
      }// , error => this.onError('Błąd przy pobieraniu programów', error)
    );
  }

  onRegistrationProgrammeChange(event: MatSelectChange): void {
    this.usosService.getAvailableStages(event.value.usosId).pipe(
      tap(results => this.stages = results)
    ).subscribe(
      () => {
        this.importCreationFormGroup.patchValue({stage: null});
      }// , error => this.onError('Błąd przy pobieraniu etapów', error)
    );
  }

  onDidacticCycleInputChanges(): void {
    this.changesSubscription = this.importCreationFormGroup.get('didacticCycle').valueChanges.pipe(
      filter(value => value !== undefined && value !== '' && value !== null && value.length >= 2),
      switchMap(value => this.usosService.findDidacticCycleCodes(value)),
      tap(didacticCycles => this.didacticCycles = didacticCycles)
    ).subscribe(
      () => {
      }// , error => this.onError('Błąd przy pobieraniu cykli dydaktycznych', error)
    );
  }

  onSubmit() {
    this.import.registration = this.importCreationFormGroup.value.registration;
    this.import.programmeCode = this.importCreationFormGroup.value.registrationProgramme.usosId;
    this.import.programmeForeignId = this.importCreationFormGroup.value.registrationProgramme.id;
    this.import.didacticCycleCode = this.importCreationFormGroup.value.didacticCycle;
    this.import.dateOfAddmision = this.importCreationFormGroup.value.dateOfAddmision;
    this.import.startDate = this.importCreationFormGroup.value.startDate;
    this.import.indexPoolCode = this.importCreationFormGroup.value.indexPoolCode;
    this.import.stageCode = this.importCreationFormGroup.value.stage;
    this.import.dataSourceId = this.importCreationFormGroup.value.dataSource;
    this.importService.createImport(this.import).subscribe(
      importObject => this.onImportCreated(importObject),
      // error => this.onError('Błąd przy tworzeniu importu', error)
    );
  }

  // onError(title: string, error): void {
  //   if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
  //     return;
  //   }
  //   this.dialog.open(ErrorDialogComponent, {
  //     data: new ErrorDialogData(title, error)
  //   });
  // }

  onImportCreated(importObject: Import): void {
    const snackBarRef = this.snackBar.open('Import utworzony', 'OK', {
      duration: 3000
    });
    snackBarRef.onAction().subscribe(() => snackBarRef.dismiss());
    this.formGroupDirective.resetForm();
    this.import = importObject;
    this.importCreated.next(this.import);
  }

  ngOnDestroy(): void {
    this.snackBar.ngOnDestroy();
    this.changesSubscription.unsubscribe();
  }

  onDataSourceSelectionChange(event: MatSelectChange) {
    this.dataSourceId = event.value;
    this.importService.getAvailableRegistrations(event.value).pipe(
      tap(results => this.registrations = results)
    ).subscribe(
      () => {
        this.importCreationFormGroup.patchValue({registration: null, registrationProgramme: null, stage: null});
      }// , error => this.onError('Błąd przy pobieraniu rekrutacji', error)
    );
  }
}
