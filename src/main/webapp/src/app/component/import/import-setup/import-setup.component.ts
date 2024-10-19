import {Component, inject, input, OnDestroy, OnInit, output, signal, viewChild} from '@angular/core';
import {Import} from '../../../model/import/import';
import {ImportService} from '../../../service/import-service/import.service';
import {
  FormControl,
  FormGroup,
  FormGroupDirective,
  ReactiveFormsModule,
  UntypedFormControl,
  UntypedFormGroup,
  Validators
} from '@angular/forms';
import {debounceTime, distinctUntilChanged, finalize, forkJoin, Observable, of, Subscription} from 'rxjs';
import {map, switchMap, tap} from 'rxjs/operators';
import {IndexType} from '../../../model/oracle/index-type';
import {Registration} from '../../../model/applications/registration';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatSelect, MatSelectChange} from '@angular/material/select';
import {DataSource, DataSourceAdditionalParameter} from '../../../model/import/dataSource';
import {Programme} from '../../../model/applications/programme';
import {UsosService} from '../../../service/usos-service/usos.service';
import {APP_BASE_HREF, AsyncPipe} from '@angular/common';
import {MatFormField, MatLabel, MatPrefix, MatSuffix} from '@angular/material/form-field';
import {MatOption} from '@angular/material/core';
import {MatInput} from '@angular/material/input';
import {ReactiveFileInputComponent} from '../../reactive-file-input/reactive-file-input.component';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatAutocomplete, MatAutocompleteTrigger} from '@angular/material/autocomplete';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker';

@Component({
  selector: 'app-import-setup',
  templateUrl: './import-setup.component.html',
  styleUrls: ['./import-setup.component.sass'],
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatSelect,
    MatOption,
    MatInput,
    ReactiveFileInputComponent,
    MatPrefix,
    MatProgressSpinner,
    MatAutocompleteTrigger,
    MatButton,
    MatSuffix,
    MatIcon,
    MatAutocomplete,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatDatepicker,
    AsyncPipe
  ]
})
export class ImportSetupComponent implements OnInit, OnDestroy {
  private readonly importService = inject(ImportService);
  private readonly usosService = inject(UsosService);
  private readonly snackBar = inject(MatSnackBar);
  readonly baseHref = inject(APP_BASE_HREF);


  dataSourceId = '?';
  $availableDataSources: Observable<[DataSource]> = this.importService.getAvailableDataSources();
  registrations: Registration[] = [];
  registrationProgrammes: Programme[] = [];
  $indexPools: Observable<[IndexType]> = this.usosService.getAvailableIndexPools();
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
  }> = new FormGroup({
    dataSource: new FormControl<DataSource | null>(null, Validators.required),
    registration: new FormControl<string | null>(null, Validators.required),
    registrationProgramme: new FormControl<Programme | null>(null, Validators.required),
    indexPoolCode: new FormControl<IndexType | null>(null),
    stage: new FormControl<string | null>(null, Validators.required),
    didacticCycle: new FormControl<string | null>(null, Validators.required),
    startDate: new FormControl<Date | null>(null, Validators.required),
    dateOfAddmision: new FormControl<Date | null>(null, Validators.required),
    additionalParameters: new UntypedFormGroup({})
  });
  isButtonDisabled = signal(false);
  areRegistrationLoading = signal(false);
  areProgrammesLoading = signal(false);
  areStagesLoading = signal(false);
  subs: Subscription[] = [];
  additionalParameters = signal<DataSourceAdditionalParameter[]>([]);

  importCreated = output<Import>();
  import = input<Import | null>();
  formGroupDirective = viewChild<FormGroupDirective | null>(FormGroupDirective);

  ngOnInit(): void {
    this.subs.push(this.onDidacticCycleInputChanges().subscribe());
    const importId = this.import()?.id;
    if (importId != null) {
      this.subs.push(this.initForm(importId).subscribe());
    }
  }

  onRegistrationSelectionChange(event: MatSelectChange): void {
    this.subs.push(
      this.getAvailableRegistrationProgrammes(event.value, this.dataSourceId).pipe(
        tap(() => this.areProgrammesLoading.set(true)),
        tap(() => this.formGroup.patchValue({registrationProgramme: null, stage: null})),
        finalize(() => this.areProgrammesLoading.set(false))
      ).subscribe()
    );
  }

  onRegistrationProgrammeChange(event: MatSelectChange): void {
    this.subs.push(
      this.getProgrammeStages(event.value.usosId).pipe(
        tap(() => this.areStagesLoading.set(true)),
        tap(() => this.formGroup.patchValue({stage: null})),
        finalize(() => this.areStagesLoading.set(false))
      ).subscribe()
    );
  }

  onDidacticCycleInputChanges(): Observable<[string]> {
    return this.formGroup.controls.didacticCycle.valueChanges.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      switchMap(value => this.usosService.findDidacticCycleCodes(value ?? '')),
      tap(didacticCycles => this.didacticCycles = didacticCycles)
    );
  }

  onSubmit() {
    this.isButtonDisabled.set(true);
    const value = this.formGroup.value;
    const newImport: Import = {
      id: this.import()?.id ?? null,
      registration: value.registration ?? null,
      programmeCode: value.registrationProgramme?.usosId ?? null,
      programmeForeignId: value.registrationProgramme?.id ?? null,
      programmeForeignName: value.registrationProgramme?.name ?? null,
      didacticCycleCode: value.didacticCycle ?? null,
      dateOfAddmision: value.dateOfAddmision ?? null,
      startDate: value.startDate ?? null,
      indexPoolCode: value.indexPoolCode?.code ?? null,
      indexPoolName: value.indexPoolCode?.description ?? null,
      stageCode: value.stage ?? null,
      dataSourceId: value.dataSource?.id ?? null,
      dataSourceName: value.dataSource?.name ?? null,
      additionalProperties: this.formGroup.controls.additionalParameters.value,
      importedApplications: 0,
      saveErrors: 0,
      savedApplicants: 0,
      totalCount: null,
      importStatus: null,
      importedUids: 0,
      notificationsSend: 0,
      potentialDuplicates: 0,
      error: null,
      stackTrace: null
    };
    let action: Observable<Import>;
    if (this.import()?.id == null) {
      action = this.importService.create(newImport);
    } else {
      action = this.importService.update(newImport);
    }
    action?.pipe(
      tap((importObj: Import) => this.onImportCreated(importObj))
    ).subscribe();
  }

  onImportCreated(importObject: Import): void {
    const snackBarRef = this.snackBar.open('Import utworzony', 'OK', {
      duration: 3000
    });
    this.subs.push(
      snackBarRef.onAction().subscribe(() => snackBarRef.dismiss())
    );
    this.formGroupDirective()?.resetForm();
    this.importCreated.emit(importObject);
    this.isButtonDisabled.set(false);
  }

  ngOnDestroy(): void {
    this.snackBar.ngOnDestroy();
    this.subs.forEach(subscription => subscription.unsubscribe());
  }

  onDataSourceSelectionChange(event: MatSelectChange) {
    const dataSource: DataSource = event.value;
    this.dataSourceId = dataSource.id;
    this.initAdditionalParameters(dataSource);
    this.subs.push(
      this.getAvailableRegistrations(dataSource.id).pipe(
        tap(() => this.areRegistrationLoading.set(true)),
        tap(() => this.formGroup.patchValue({registration: null, registrationProgramme: null, stage: null})),
        finalize(() => this.areRegistrationLoading.set(false))
      ).subscribe()
    );
  }

  getUrlWithBaseHref(url: string | undefined): string {
    if (this.baseHref) {
      return `${this.baseHref}${url}`;
    }
    return url ?? '';
  }

  datasourceCompare(ds1?: DataSource, ds2?: DataSource): boolean {
    return ds1 && ds2 ? ds1.id === ds2.id : false;
  }

  registrationCompare(rg1?: Registration, rg2?: Registration): boolean {
    return rg1 && rg2 ? rg1.id === rg2.id : false;
  }

  registrationProgrammeCompare(rgp1?: Programme, rgp2?: Programme): boolean {
    return rgp1 && rgp2 ? rgp1.id === rgp2.id : false;
  }

  indexTypeCompare(it1?: IndexType, it2?: IndexType): boolean {
    return it1 && it2 ? it1.code === it2.code : false;
  }

  initForm(importId: number): Observable<any> {
    return this.importService.findById(importId).pipe(
      switchMap(importObj =>
        forkJoin([
          of(importObj),
          this.$availableDataSources.pipe(
            map(dataSources => dataSources.find(d => d.id === importObj.dataSourceId)),
            tap(datasource => this.initAdditionalParameters(datasource))
          ),
          this.getAvailableRegistrations(importObj.dataSourceId ?? '?'),
          this.getAvailableRegistrationProgrammes(importObj.registration ?? '?', importObj.dataSourceId ?? '?'),
          this.getProgrammeStages(importObj.programmeCode ?? '?')
        ])),
      tap(data => {
        const importObj: Import = data[0];
        const dataSource: DataSource | undefined = data[1];
        this.formGroup.patchValue(
          {
            dataSource,
            registration: importObj.registration,
            registrationProgramme: new Programme(
              importObj.programmeForeignId ?? '?',
              importObj.programmeForeignName ?? '?',
              importObj.programmeCode ?? '?'
            ),
            indexPoolCode: new IndexType(importObj.indexPoolCode ?? '', importObj.indexPoolName ?? '?'),
            stage: importObj.stageCode,
            didacticCycle: importObj.didacticCycleCode,
            startDate: importObj.startDate,
            dateOfAddmision: importObj.dateOfAddmision,
            additionalParameters: importObj.additionalProperties
          }
        );
      })
    );
  }

  private getProgrammeStages(programmeCode: string) {
    return this.usosService.getAvailableStages(programmeCode).pipe(
      tap(results => this.stages = results)
    );
  }

  private getAvailableRegistrationProgrammes(registration: string, dataSourceId: string) {
    return this.importService.getAvailableRegistrationProgrammes(registration, dataSourceId).pipe(
      tap(results => this.registrationProgrammes = results)
    );
  }

  private getAvailableRegistrations(dataSourceId: string) {
    return this.importService.getAvailableRegistrations(dataSourceId).pipe(
      tap(results => this.registrations = results)
    );
  }

  initAdditionalParameters(dataSource: DataSource | undefined) {
    if (dataSource == null) {
      return;
    }
    const additionalParametersFG: UntypedFormGroup = this.formGroup.controls.additionalParameters as UntypedFormGroup;
    this.additionalParameters().forEach(additionalParameter => {
      additionalParametersFG.removeControl(additionalParameter.name);
    });
    this.additionalParameters.set(dataSource.additionalParameters);
    dataSource.additionalParameters.forEach(additionalParameter => {
      const fc = new UntypedFormControl(additionalParameter.value, Validators.required);
      additionalParametersFG.addControl(additionalParameter.name, fc);
    });
  }
}
