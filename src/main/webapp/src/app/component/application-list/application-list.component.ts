import {ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit} from '@angular/core';
import {BasicDataSource} from '../../dataSource/basic-data-source';
import {Application} from '../../model/applications/application';
import {BehaviorSubject, debounceTime, distinctUntilChanged, tap} from 'rxjs';
import {UrlDto} from '../../model/import/urlDto';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort, MatSortHeader} from '@angular/material/sort';
import {UsosService} from '../../service/usos-service/usos.service';
import {UserService} from '../../service/user-service/user.service';
import {MatDialog} from '@angular/material/dialog';
import {ApplicationsService} from '../../service/application-service/applications.service';
import {AbstractListWithPathParamsComponent} from '../abstract-list-with-path-params.component';
import {ErrorDialogComponent} from '../dialog/error-dialog/error-dialog.component';
import {ErrorDialogData} from '../../model/dialog/error-dialog-data';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {APP_BASE_HREF, AsyncPipe, DatePipe} from '@angular/common';
import {MatFormField, MatLabel} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from '@angular/material/table';
import {MatAnchor, MatButton} from '@angular/material/button';
import {MatTooltip} from '@angular/material/tooltip';
import {MatIcon} from '@angular/material/icon';

type filterType = {
  importId?: number | null,
  name?: string | null,
  surname?: string | null,
  indexNumber?: string | null,
  pesel?: string | null,
};

@Component({
  selector: 'app-application-list',
  templateUrl: './application-list.component.html',
  styleUrls: ['./application-list.component.sass'],
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatInput,
    MatTable,
    MatSort,
    MatColumnDef,
    MatHeaderCellDef,
    MatHeaderCell,
    MatCellDef,
    MatCell,
    MatSortHeader,
    MatAnchor,
    MatTooltip,
    MatIcon,
    MatButton,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRowDef,
    MatRow,
    MatPaginator,
    DatePipe,
    AsyncPipe
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApplicationListComponent extends AbstractListWithPathParamsComponent<Application, number, filterType>
  implements OnInit, OnDestroy {
  private readonly usosService = inject(UsosService);
  protected readonly userService = inject(UserService);
  private readonly dialog = inject(MatDialog);
  protected readonly baseHref = inject(APP_BASE_HREF);
  private readonly applicationsService = inject(ApplicationsService);
  dataSource: BasicDataSource<Application, number, filterType> =
    new BasicDataSource<Application, number, filterType>(this.applicationsService);
  filtersSubject: BehaviorSubject<filterType> = new BehaviorSubject<filterType>({});
  usosUrl$ = this.usosService.getUsosUrl();
  displayedColumns: Map<string, boolean> = new Map<string, boolean>([
    ['lp', true],
    ['id', true],
    ['foreignId', true],
    ['importId', true],
    ['usosId', true],
    ['uid', true],
    ['names', true],
    ['birthDateAndPlace', true],
    ['pesel', true],
    ['certificateDocumentNumber', false],
    ['certificateDocumentIssueDateAndPlace', false],
    ['certificateDocumentIssueInstitution', false],
    ['indexNumber', true],
    ['applicationImportStatus', true],
    ['duplicateStatus', true],
    ['warnings', true]
  ]);
  sortingMap: Map<string, string> = new Map<string, string>([
    ['id', 'id'],
    ['foreignId', 'foreignId'],
    ['uid', 'applicant.uid'],
    ['usosId', 'applicant.usosId'],
    ['names', 'applicant.family'],
    ['birthDateAndPlace', 'applicant.dateOfBirth'],
    ['pesel', 'applicant.pesel'],
    ['indexNumber', 'applicant.assignedIndexNumber'],
    ['applicationImportStatus', 'importStatus'],
    ['duplicateStatus', 'applicant.potentialDuplicateStatus'],
    ['warnings', 'warnings']
  ]);
  filterFormGroup: FormGroup<{
    importId: FormControl<number | null>,
    name: FormControl<string | null>,
    surname: FormControl<string | null>,
    pesel: FormControl<string | null>
  }> = new FormGroup({
    importId: new FormControl<number | null>(null),
    name: new FormControl<string | null>(null),
    surname: new FormControl<string | null>(null),
    pesel: new FormControl<string | null>(null)
  });

  ngOnInit(): void {
    this.subs.push(
      this.filterFormGroup.valueChanges.pipe(
        distinctUntilChanged(),
        debounceTime(200),
        tap(value => this.filtersSubject.next(value))
      ).subscribe()
    );
  }

  getPersonUsosUrl(application: Application, usosUrl: UrlDto): string {
    return `${usosUrl?.url}/studenci/programyOsob.jsf?osobaId=${application.applicant.usosId}`;
  }

  getPeselOrIdNumber(application: Application) {
    if (application.applicant.pesel != null) {
      return application.applicant.pesel;
    } else {
      return application.applicant.primaryIdentityDocument?.number;
    }
  }

  showApplicationErrorDialog(application: Application): void {
    if (application.importStatus === 'ERROR') {
      if (this.dialog.openDialogs.length > 0) {
        return;
      }
      this.dialog.open(ErrorDialogComponent, {
        data: new ErrorDialogData('Błąd przy importowaniu', application.importError, application.stackTrace)
      });
    }
  }
}
