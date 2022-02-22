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
  formGroup: FormGroup;
  changesSubscription: Subscription;
  isButtonDisabled = false;
  areRegistrationLoading = false;
  areProgrammesLoading = false;
  areStagesLoading = false;
  subs: Subscription[] = [];

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
    this.formGroup = this.formBuilder.group({
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
    this.subs.push(
      this.formGroup.controls.dataFile.valueChanges.subscribe((fileInput: any) => {
        const reader = new FileReader();

        if (fileInput) {
          reader.readAsDataURL(fileInput);
          reader.onload = () => {
            this.import.dataFile = reader.result.toString().replace(/^data:(.*,)?/, '');
            this.cd.markForCheck();
          };
        }
      })
    );
  }

  onRegistrationSelectionChange(event: MatSelectChange): void {
    this.areProgrammesLoading = true;
    this.subs.push(
      this.importService.getAvailableRegistrationProgrammes(event.value, this.dataSourceId).pipe(
        tap(results => this.registrationProgrammes = results)
      ).subscribe(
        () => {
          this.formGroup.patchValue({registrationProgramme: null, stage: null});
          this.areProgrammesLoading = false;
        },
        error => {
          this.areProgrammesLoading = false;
          throw error;
        }
      )
    );
  }

  onRegistrationProgrammeChange(event: MatSelectChange): void {
    this.areStagesLoading = true;
    this.subs.push(
      this.usosService.getAvailableStages(event.value.usosId).pipe(
        tap(results => this.stages = results)
      ).subscribe(
        () => {
          this.formGroup.patchValue({stage: null});
          this.areStagesLoading = false;
        },
        error => {
          this.areStagesLoading = false;
          throw error;
        }
      )
    );
  }

  onDidacticCycleInputChanges(): void {
    this.subs.push(
      this.formGroup.get('didacticCycle').valueChanges.pipe(
        filter(value => value !== undefined && value !== '' && value !== null && value.length >= 2),
        switchMap(value => this.usosService.findDidacticCycleCodes(value)),
        tap(didacticCycles => this.didacticCycles = didacticCycles)
      ).subscribe()
    );
  }

  onSubmit() {
    this.isButtonDisabled = true;
    this.import.registration = this.formGroup.value.registration;
    this.import.programmeCode = this.formGroup.value.registrationProgramme.usosId;
    this.import.programmeForeignId = this.formGroup.value.registrationProgramme.id;
    this.import.programmeForeignName = this.formGroup.value.registrationProgramme.name;
    this.import.didacticCycleCode = this.formGroup.value.didacticCycle;
    this.import.dateOfAddmision = this.formGroup.value.dateOfAddmision;
    this.import.startDate = this.formGroup.value.startDate;
    this.import.indexPoolCode = this.formGroup.value.indexPoolCode.code;
    this.import.indexPoolName = this.formGroup.value.indexPoolCode.description;
    this.import.stageCode = this.formGroup.value.stage;
    this.import.dataSourceId = this.formGroup.value.dataSource;
    this.importService.createImport(this.import).subscribe(
      importObject => this.onImportCreated(importObject),
      error => {
        this.isButtonDisabled = false;
        throw error;
      }
    );
  }

  onImportCreated(importObject: Import): void {
    const snackBarRef = this.snackBar.open('Import utworzony', 'OK', {
      duration: 3000
    });
    this.subs.push(
      snackBarRef.onAction().subscribe(() => snackBarRef.dismiss())
    );
    this.formGroupDirective.resetForm();
    this.import = importObject;
    this.importCreated.next(this.import);
    this.isButtonDisabled = false;
  }

  ngOnDestroy(): void {
    this.snackBar.ngOnDestroy();
    this.subs.forEach(subscription => subscription.unsubscribe());
  }

  onDataSourceSelectionChange(event: MatSelectChange) {
    this.dataSourceId = event.value;
    this.areRegistrationLoading = true;
    this.subs.push(
      this.importService.getAvailableRegistrations(event.value).pipe(
        tap(results => this.registrations = results)
      ).subscribe(
        () => {
          this.formGroup.patchValue({registration: null, registrationProgramme: null, stage: null});
          this.areRegistrationLoading = false;
        },
        error => {
          this.areRegistrationLoading = false;
          throw error;
        }
      )
    );
  }
}
