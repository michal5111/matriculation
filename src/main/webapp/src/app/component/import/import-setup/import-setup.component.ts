import {Component, EventEmitter, Inject, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {Import} from '../../../model/import/import';
import {ImportService} from '../../../service/import-service/import.service';
import {FormControl, FormGroup, FormGroupDirective, Validators} from '@angular/forms';
import {concat, Observable, Observer, Subscription} from 'rxjs';
import {filter, flatMap, switchMap, tap} from 'rxjs/operators';
import {IndexType} from '../../../model/oracle/index-type';
import {Registration} from '../../../model/applications/registration';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatSelectChange} from '@angular/material/select';
import {DataSource, DataSourceAdditionalParameter} from '../../../model/import/dataSource';
import {Programme} from '../../../model/applications/programme';
import {UsosService} from '../../../service/usos-service/usos.service';
import {fromArray} from 'rxjs/internal/observable/fromArray';
import {APP_BASE_HREF} from '@angular/common';

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
  isButtonDisabled = false;
  areRegistrationLoading = false;
  areProgrammesLoading = false;
  areStagesLoading = false;
  subs: Subscription[] = [];
  additionalParameters: DataSourceAdditionalParameter[] = [];

  constructor(
    private importService: ImportService,
    private usosService: UsosService,
    private snackBar: MatSnackBar,
    @Inject(APP_BASE_HREF) public baseHref
    // private dialog: MatDialog,
    // private cd: ChangeDetectorRef
  ) {
  }

  @Output() importCreated = new EventEmitter<Import>();
  @ViewChild(FormGroupDirective) formGroupDirective: FormGroupDirective;

  ngOnInit(): void {
    this.formGroup = new FormGroup({
      dataSource: new FormControl(this.import.dataSourceId, Validators.required),
      registration: new FormControl(this.import.registration, Validators.required),
      registrationProgramme: new FormControl(this.import.programmeForeignId, Validators.required),
      indexPoolCode: new FormControl(this.import.indexPoolCode, Validators.required),
      stage: new FormControl(this.import.stageCode, Validators.required),
      didacticCycle: new FormControl(this.import.didacticCycleCode, Validators.required),
      startDate: new FormControl(this.import.startDate, Validators.required),
      dateOfAddmision: new FormControl(this.import.dateOfAddmision, Validators.required),
      additionalParameters: new FormGroup({})
    });
    this.onDidacticCycleInputChanges();
  }

  onRegistrationSelectionChange(event: MatSelectChange): void {
    this.areProgrammesLoading = true;
    this.subs.push(
      this.importService.getAvailableRegistrationProgrammes(event.value, this.dataSourceId).pipe(
        tap(results => this.registrationProgrammes = results)
      ).subscribe({
        next: () => {
          this.formGroup.patchValue({registrationProgramme: null, stage: null});
        },
        complete: () => {
          this.areProgrammesLoading = false;
        }
      })
    );
  }

  onRegistrationProgrammeChange(event: MatSelectChange): void {
    this.areStagesLoading = true;
    this.subs.push(
      this.usosService.getAvailableStages(event.value.usosId).pipe(
        tap(results => this.stages = results)
      ).subscribe({
        next: () => {
          this.formGroup.patchValue({stage: null});
        },
        complete: () => {
          this.areStagesLoading = false;
        }
      })
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
    this.import.dataSourceId = this.formGroup.value.dataSource.id;
    this.import.additionalProperties = this.formGroup.controls.additionalParameters.value;
    const observables: Observable<string>[] = [];
    console.log(this.formGroup.controls.additionalParameters.value);
    Object.keys(this.import.additionalProperties).forEach(key => {
      const ap = this.import.additionalProperties[key];
      if (ap instanceof File) {
        console.log(ap);
        observables.push(
          this.getBase64FromFile(ap).pipe(
            tap(base64string => this.import.additionalProperties[key] = base64string)
          )
        );
      }
    });
    concat(
      fromArray(observables).pipe(
        flatMap(ob => ob)
      ),
      this.importService.createImport(this.import).pipe(
        tap((importObj: Import) => this.onImportCreated(importObj))
      )
    ).subscribe({
        error: () => {
          this.isButtonDisabled = false;
        }
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
    const dataSource: DataSource = event.value;
    const additionalParametersFG: FormGroup = this.formGroup.controls.additionalParameters as FormGroup;
    this.dataSourceId = dataSource.id;
    this.additionalParameters.forEach(additionalParameter => {
      additionalParametersFG.removeControl(additionalParameter.name);
    });
    this.additionalParameters = dataSource.additionalParameters;
    dataSource.additionalParameters.forEach(additionalParameter => {
      const fc = new FormControl(additionalParameter.value, Validators.required);
      additionalParametersFG.addControl(additionalParameter.name, fc);
      if (additionalParameter.type === 'FILE') {
        const fc2 = new FormControl(null, Validators.required);
        additionalParametersFG.addControl(additionalParameter.name + 'Source', fc2);
      }
    });
    console.log(additionalParametersFG);
    this.areRegistrationLoading = true;
    this.subs.push(
      this.importService.getAvailableRegistrations(dataSource.id).pipe(
        tap(results => this.registrations = results)
      ).subscribe({
          next: () => {
            this.formGroup.patchValue({registration: null, registrationProgramme: null, stage: null});
          },
          complete: () => {
            this.areRegistrationLoading = false;
          }
        }
      ));
  }

  getBase64FromFile(file: File): Observable<string> {
    return new Observable<string>((observer: Observer<string>) => {
      const reader = new FileReader();
      if (file) {
        reader.onload = () => {
          observer.next(reader.result.toString());
          observer.complete();
        };
        reader.readAsDataURL(file);
      }
    });
  }

  getUrlWithBaseHref(url: string): string {
    if (this.baseHref) {
      return `${this.baseHref}${url}`;
    }
    return url;
  }

  onFileSelected(event, parameterName: string) {
    const file: File = event.target.files[0];
    if (file) {
      const additionalParametersFG: FormGroup = this.formGroup.controls.additionalParameters as FormGroup;
      const pw = {};
      pw[parameterName + 'Source'] = file;
      additionalParametersFG.patchValue(pw);
    }
  }
}
