import {Component, EventEmitter, Inject, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {Import} from '../../../model/import/import';
import {ImportService} from '../../../service/import-service/import.service';
import {
  FormControl,
  FormGroup,
  FormGroupDirective,
  UntypedFormControl,
  UntypedFormGroup,
  Validators
} from '@angular/forms';
import {debounceTime, distinctUntilChanged, from, Observable, Subscription} from 'rxjs';
import {map, switchMap, tap} from 'rxjs/operators';
import {IndexType} from '../../../model/oracle/index-type';
import {Registration} from '../../../model/applications/registration';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatSelectChange} from '@angular/material/select';
import {DataSource, DataSourceAdditionalParameter} from '../../../model/import/dataSource';
import {Programme} from '../../../model/applications/programme';
import {UsosService} from '../../../service/usos-service/usos.service';
import {APP_BASE_HREF} from '@angular/common';

@Component({
  selector: 'app-import-setup',
  templateUrl: './import-setup.component.html',
  styleUrls: ['./import-setup.component.sass']
})
export class ImportSetupComponent implements OnInit, OnDestroy {

  dataSourceId = '?';
  $availableDataSourcesObservable: Observable<[DataSource]> = this.importService.getAvailableDataSources();
  registrations: Registration[] = [];
  registrationProgrammes: Programme[] = [];
  $indexPoolsObservable: Observable<[IndexType]> = this.usosService.getAvailableIndexPools();
  stages: string[] = [];
  didacticCycles: string[] = [];
  formGroup: FormGroup<{
    dataSource: FormControl<DataSource | null>,
    registration: FormControl<string | null>,
    registrationProgramme: FormControl<Programme | null>,
    indexPoolCode: FormControl<IndexType | null>,
    stage: FormControl<string | null>,
    didacticCycle: FormControl<string | null>,
    startDate: FormControl<Date | null>,
    dateOfAddmision: FormControl<Date | null>,
    additionalParameters: UntypedFormGroup
  }>;
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
    @Inject(APP_BASE_HREF) public baseHref: string
  ) {
    this.formGroup = new FormGroup({
      dataSource: new FormControl<DataSource | null>(null, Validators.required),
      registration: new FormControl<string | null>(null, Validators.required),
      registrationProgramme: new FormControl<Programme | null>(null, Validators.required),
      indexPoolCode: new FormControl<IndexType | null>(null, Validators.required),
      stage: new FormControl<string | null>(null, Validators.required),
      didacticCycle: new FormControl<string | null>(null, Validators.required),
      startDate: new FormControl<Date | null>(null, Validators.required),
      dateOfAddmision: new FormControl<Date | null>(null, Validators.required),
      additionalParameters: new UntypedFormGroup({})
    });
  }

  @Output() importCreated = new EventEmitter<Import>();
  @Input() import: Import | null = new Import();
  @ViewChild(FormGroupDirective) formGroupDirective: FormGroupDirective | null = null;

  ngOnInit(): void {
    this.subs.push(this.onDidacticCycleInputChanges().subscribe());
    if (this.import != null && this.import.id != null) {
      this.subs.push(this.initForm(this.import.id).subscribe());
    }
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

  onDidacticCycleInputChanges(): Observable<[string]> {
    return this.formGroup.get('didacticCycle')?.valueChanges.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      switchMap(value => this.usosService.findDidacticCycleCodes(value ?? '')),
      tap(didacticCycles => this.didacticCycles = didacticCycles)
    ) ?? from([]);
  }

  onSubmit() {
    this.isButtonDisabled = true;
    if (this.import == null) {
      throw Error();
    }
    this.import.registration = this.formGroup.value.registration ?? null;
    this.import.programmeCode = this.formGroup.value.registrationProgramme?.usosId ?? null;
    this.import.programmeForeignId = this.formGroup.value.registrationProgramme?.id ?? null;
    this.import.programmeForeignName = this.formGroup.value.registrationProgramme?.name ?? null;
    this.import.didacticCycleCode = this.formGroup.value.didacticCycle ?? null;
    this.import.dateOfAddmision = this.formGroup.value.dateOfAddmision ?? null;
    this.import.startDate = this.formGroup.value.startDate ?? null;
    this.import.indexPoolCode = this.formGroup.value.indexPoolCode?.code ?? null;
    this.import.indexPoolName = this.formGroup.value.indexPoolCode?.description ?? null;
    this.import.stageCode = this.formGroup.value.stage ?? null;
    this.import.dataSourceId = this.formGroup.value.dataSource?.id ?? null;
    this.import.dataSourceName = this.formGroup.value.dataSource?.name ?? null;
    this.import.additionalProperties = this.formGroup.controls.additionalParameters.value;
    console.log(this.formGroup.controls.additionalParameters.value);
    let obs;
    if (this.import?.id == null) {
      obs = this.importService.createImport(this.import);
    } else {
      obs = this.importService.updateImport(this.import);
    }
    obs?.pipe(
      tap((importObj: Import) => this.onImportCreated(importObj))
    ).subscribe({
        next: () => this.import = new Import(),
        error: e => {
          console.log(e);
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
    this.formGroupDirective?.resetForm();
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
    this.dataSourceId = dataSource.id;
    this.initAdditionalParameters(dataSource);
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

  getUrlWithBaseHref(url: string | undefined): string {
    if (this.baseHref) {
      return `${this.baseHref}${url}`;
    }
    return url ?? '';
  }

  datasourceCompare(ds1: DataSource, ds2: DataSource): boolean {
    return ds1 && ds2 ? ds1.id === ds2.id : false;
  }

  registrationCompare(rg1: Registration, rg2: Registration): boolean {
    return rg1 && rg2 ? rg1.id === rg2.id : false;
  }

  registrationProgrammeCompare(rgp1: Programme, rgp2: Programme): boolean {
    return rgp1 && rgp2 ? rgp1.id === rgp2.id : false;
  }

  indexTypeCompare(it1: IndexType, it2: IndexType): boolean {
    return it1.code === it2.code;
  }

  initForm(importId: number): Observable<Import> {
    return this.importService.findById(importId).pipe(
      tap(value => this.import = value),
      switchMap(value =>
        this.$availableDataSourcesObservable.pipe(
          map(dataSources => dataSources.find(d => d.id === value.dataSourceId)),
          tap(datasource => this.initAdditionalParameters(datasource)),
          switchMap(() => this.importService.getAvailableRegistrations(value.dataSourceId ?? '')),
          tap(results => this.registrations = results),
          switchMap(() => this.importService.getAvailableRegistrationProgrammes(
            value.registration ?? '',
            value.dataSourceId ?? ''
          )),
          tap(results => this.registrationProgrammes = results),
          switchMap(() => this.usosService.getAvailableStages(value.programmeCode ?? '').pipe(
            tap(results => this.stages = results)
          )),
          tap(results => this.stages = results),
          map(() => value)
        )
      ),
      tap(value => {
        this.formGroup.patchValue(
          {
            dataSource: new DataSource(value.dataSourceName ?? '?', value.dataSourceId ?? '?'),
            registration: value.registration,
            registrationProgramme: new Programme(
              value.programmeForeignId ?? '?',
              value.programmeForeignName ?? '?',
              value.programmeCode ?? '?'
            ),
            indexPoolCode: new IndexType(value.indexPoolCode ?? '', value.indexPoolName ?? '?'),
            stage: value.stageCode,
            didacticCycle: value.didacticCycleCode,
            startDate: value.startDate,
            dateOfAddmision: value.dateOfAddmision,
            additionalParameters: value.additionalProperties
          }
        );
      })
    );
  }

  initAdditionalParameters(dataSource: DataSource | undefined) {
    if (dataSource == null) {
      return;
    }
    const additionalParametersFG: UntypedFormGroup = this.formGroup.controls.additionalParameters as UntypedFormGroup;
    this.additionalParameters.forEach(additionalParameter => {
      additionalParametersFG.removeControl(additionalParameter.name);
    });
    this.additionalParameters = dataSource.additionalParameters;
    dataSource.additionalParameters.forEach(additionalParameter => {
      const fc = new UntypedFormControl(additionalParameter.value, Validators.required);
      additionalParametersFG.addControl(additionalParameter.name, fc);
    });
  }
}
