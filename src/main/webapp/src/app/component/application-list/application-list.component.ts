import {Component, Inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {BasicDataSource} from '../../dataSource/basic-data-source';
import {Application} from '../../model/applications/application';
import {BehaviorSubject, debounceTime, distinctUntilChanged, tap} from 'rxjs';
import {UrlDto} from '../../model/import/urlDto';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {UsosService} from '../../service/usos-service/usos.service';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../service/user-service/user.service';
import {MatDialog} from '@angular/material/dialog';
import {ApplicationsService} from '../../service/application-service/applications.service';
import {AbstractListWithPathParamsComponent} from '../abstract-list-with-path-params.component';
import {ErrorDialogComponent} from '../dialog/error-dialog/error-dialog.component';
import {ErrorDialogData} from '../../model/dialog/error-dialog-data';
import {FormControl, FormGroup} from '@angular/forms';
import {APP_BASE_HREF} from '@angular/common';

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
  styleUrls: ['./application-list.component.sass']
})
export class ApplicationListComponent extends AbstractListWithPathParamsComponent<Application, number, filterType>
  implements OnInit, OnDestroy {

  constructor(
    private usosService: UsosService,
    protected override route: ActivatedRoute,
    protected override router: Router,
    public userService: UserService,
    private dialog: MatDialog,
    applicationsService: ApplicationsService,
    @Inject(APP_BASE_HREF) public baseHref: string
  ) {
    super(route, router);
    this.filtersSubject = new BehaviorSubject<filterType>({});
    this.dataSource = new BasicDataSource<Application, number, filterType>(applicationsService);
    this.filterFormGroup = new FormGroup({
      importId: new FormControl<number | null>(null),
      name: new FormControl<string | null>(null),
      surname: new FormControl<string | null>(null),
      pesel: new FormControl<string | null>(null)
    });
  }

  dataSource: BasicDataSource<Application, number, filterType>;
  filtersSubject: BehaviorSubject<filterType>;
  usosUrl: UrlDto | null = null;
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
  @ViewChild(MatPaginator) override paginator: MatPaginator | null = null;
  @ViewChild(MatSort) override sort: MatSort | null = null;
  filterFormGroup: FormGroup<{
    importId: FormControl<number | null>,
    name: FormControl<string | null>,
    surname: FormControl<string | null>,
    pesel: FormControl<string | null>
  }>;

  protected readonly APP_BASE_HREF = APP_BASE_HREF;

  ngOnInit(): void {
    this.subs.push(
      this.usosService.getUsosUrl().subscribe(
        result => this.usosUrl = result
      ),
      this.filterFormGroup.valueChanges.pipe(
        distinctUntilChanged(),
        debounceTime(200),
        tap(value => this.filtersSubject.next(value))
      ).subscribe()
    );
  }

  getPersonUsosUrl(application: Application): string {
    return `${this.usosUrl?.url}/studenci/programyOsob.jsf?osobaId=${application.applicant.usosId}`;
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
