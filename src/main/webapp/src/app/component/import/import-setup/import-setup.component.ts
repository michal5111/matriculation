import {ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, output, signal, viewChild} from '@angular/core';
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
import {debounceTime, distinctUntilChanged, Observable, shareReplay, Subscription} from 'rxjs';
import {filter, map, switchMap, tap} from 'rxjs/operators';
import {IndexType} from '../../../model/oracle/index-type';
import {Registration} from '../../../model/applications/registration';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatSelect} from '@angular/material/select';
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
import {nonNull} from '../../../util/util';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';

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
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ImportSetupComponent implements OnInit, OnDestroy {
  private readonly importService = inject(ImportService);
  private readonly usosService = inject(UsosService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly baseHref = inject(APP_BASE_HREF);
  protected readonly dialogData = inject<{ import: Import } | null>(MAT_DIALOG_DATA, {optional: true});

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

  $availableDataSources: Observable<DataSource[]> = this.importService.getAvailableDataSources().pipe(
    shareReplay(1)
  );

  registrations$: Observable<Registration[]> = this.formGroup.controls.dataSource.valueChanges.pipe(
    filter(nonNull),
    tap(() => this.formGroup.patchValue({registration: null, registrationProgramme: null, stage: null})),
    tap(dataSource => this.initAdditionalParameters(dataSource)),
    tap(() => this.areRegistrationLoading.set(true)),
    switchMap(dataSource => this.importService.getAvailableRegistrations(dataSource.id)),
    tap(() => this.areRegistrationLoading.set(false))
  );

  registrationProgrammes$: Observable<Programme[]> = this.formGroup.controls.registration.valueChanges.pipe(
    filter(nonNull),
    tap(() => this.formGroup.patchValue({registrationProgramme: null, stage: null})),
    tap(() => this.areProgrammesLoading.set(true)),
    switchMap(registration => this.importService.getAvailableRegistrationProgrammes(
      registration,
      this.formGroup.controls.dataSource.value?.id ?? ''
    )),
    tap(() => this.areProgrammesLoading.set(false))
  );

  $indexPools: Observable<[IndexType]> = this.usosService.getAvailableIndexPools();

  stages$: Observable<string[]> = this.formGroup.controls.registrationProgramme.valueChanges.pipe(
    filter(nonNull),
    tap(() => this.areStagesLoading.set(true)),
    switchMap(programme => this.usosService.getAvailableStages(programme.usosId)),
    tap(() => this.areStagesLoading.set(false))
  );

  didacticCycles$: Observable<string[]> = this.formGroup.controls.didacticCycle.valueChanges.pipe(
    debounceTime(200),
    distinctUntilChanged(),
    switchMap(value => this.usosService.findDidacticCycleCodes(value ?? ''))
  );

  isButtonDisabled = signal(false);
  areRegistrationLoading = signal(false);
  areProgrammesLoading = signal(false);
  areStagesLoading = signal(false);
  subs: Subscription[] = [];
  additionalParameters = signal<DataSourceAdditionalParameter[]>([]);

  importCreated = output<Import>();
  formGroupDirective = viewChild<FormGroupDirective | null>(FormGroupDirective);

  ngOnInit(): void {
    if (this.dialogData?.import != null) {
      this.subs.push(this.initForm(this.dialogData?.import).subscribe());
    }
  }

  onSubmit() {
    this.isButtonDisabled.set(true);
    const value = this.formGroup.value;
    const newImport: Import = {
      id: this.dialogData?.import?.id ?? null,
      registration: value.registration ?? null,
      programmeCode: value.registrationProgramme?.usosId ?? null,
      programmeForeignId: value.registrationProgramme?.id ?? null,
      programmeForeignName: value.registrationProgramme?.name ?? null,
      didacticCycleCode: value.didacticCycle ?? null,
      dateOfAddmision: value.dateOfAddmision?.toISOString() ?? null,
      startDate: value.startDate?.toISOString() ?? null,
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
    if (this.dialogData?.import?.id == null) {
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

  initForm(importObj: Import): Observable<any> {
    return this.$availableDataSources.pipe(
      map(dataSources => dataSources.find(d => d.id === importObj.dataSourceId)),
      tap(datasource => this.initAdditionalParameters(datasource))
    ).pipe(
      tap(dataSource => {
        this.formGroup.setValue({
          dataSource: dataSource ?? null,
          registration: importObj.registration,
          registrationProgramme: {
            id: importObj.programmeForeignId ?? '?',
            name: importObj.programmeForeignName ?? '?',
            usosId: importObj.programmeCode ?? '?'
          },
          indexPoolCode: {
            code: importObj.indexPoolCode ?? '',
            description: importObj.indexPoolName ?? '?'
          },
          stage: importObj.stageCode,
          didacticCycle: importObj.didacticCycleCode,
          startDate: new Date(importObj.startDate ?? ''),
          dateOfAddmision: new Date(importObj.dateOfAddmision ?? ''),
          additionalParameters: importObj.additionalProperties
        });
      })
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
