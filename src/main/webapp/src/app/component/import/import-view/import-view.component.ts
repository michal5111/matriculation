import {Component, Inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {ImportService} from '../../../service/import-service/import.service';
import {Page} from '../../../model/oracle/page/page';
import {filter, map, switchMap, tap} from 'rxjs/operators';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatSort, Sort} from '@angular/material/sort';
import {Application} from '../../../model/applications/application';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable, Subscription} from 'rxjs';
import {Import} from '../../../model/import/import';
import {UserService} from '../../../service/user-service/user.service';
import {MatDialog} from '@angular/material/dialog';
import {
  UpdateIndexNumberDialogComponent
} from '../../dialog/update-index-number-dialog/update-index-number-dialog.component';
import {ConfirmationDialogComponent} from '../../dialog/confirmation-dialog/confirmation-dialog.component';
import {ConfirmationDialogData} from '../../../model/dialog/confirmation-dialog-data';
import {ErrorDialogComponent} from '../../dialog/error-dialog/error-dialog.component';
import {ErrorDialogData} from '../../../model/dialog/error-dialog-data';
import {UrlDto} from '../../../model/import/urlDto';
import {UsosService} from '../../../service/usos-service/usos.service';
import {SelectPersonDialogComponent} from '../../dialog/select-person-dialog/select-person-dialog.component';
import {ApplicationsService} from '../../../service/application-service/applications.service';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {RxStompService} from '@stomp/ng2-stompjs';
import {Message} from '@stomp/stompjs';
import {WS_URL} from '../../../injectableTokens/WS_URL';

@Component({
  selector: 'app-import-view',
  templateUrl: './import-view.component.html',
  styleUrls: ['./import-view.component.sass']
})
export class ImportViewComponent implements OnInit, OnDestroy {

  importId: number;
  import: Import;
  progressSubscription: Subscription;
  usosUrl: UrlDto;
  pageSize = parseInt(localStorage.getItem('importViewPageSize'), 10) ?? 5;
  pageNumber = 0;
  totalElements = 0;
  page: Page<Application>;
  dataSource = new MatTableDataSource<Application>();
  sortString = 'applicant.family';
  sortDirString = 'asc';
  displayedColumns: Map<string, boolean> = new Map<string, boolean>([
    ['lp', true],
    ['id', true],
    ['foreignId', true],
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
    ['importError', true]
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
    ['importError', 'importError'],
    ['duplicateStatus', 'applicant.potentialDuplicateStatus']
  ]);

  subs: Subscription[] = [];

  private importObservable$: Observable<Import>;

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  constructor(
    private importService: ImportService,
    private usosService: UsosService,
    private route: ActivatedRoute,
    private router: Router,
    public userService: UserService,
    private dialog: MatDialog,
    private applicationsService: ApplicationsService,
    public rxStompService: RxStompService,
    @Inject(WS_URL) private wsUrl: string
  ) {
  }

  getPage(page: number, size: number, sort?: string, sortDir?: string) {
    return this.importService.findAllApplicationsByImportId(this.importId, page, size, sort, sortDir).pipe(
      tap(applicationsPage => {
        this.page = applicationsPage;
        this.totalElements = applicationsPage.totalElements;
      }),
      map(applicationsPage => applicationsPage.content),
      tap(content => this.dataSource.data = content)
    );
  }

  getImport(importId: number): Observable<Import> {
    return this.importService.findById(importId).pipe(
      tap(importObject => this.import = importObject)
    );
  }

  ngOnInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.importId = this.route.snapshot.params.id;
    this.subs.push(
      this.getImport(this.importId).subscribe(),
      this.usosService.getUsosUrl().subscribe(
        result => this.usosUrl = result
      ),
      this.route.queryParams.pipe(
        tap(params => {
          this.pageNumber = params.page ?? this.pageNumber;
          this.sortString = params.sort ?? this.sortString;
          this.sortDirString = params.dir ?? this.sortDirString;
          this.pageSize = params.pageSize ?? this.pageSize;
        }),
        switchMap(() => this.getPage(
          this.pageNumber,
          this.pageSize,
          this.sortString,
          this.sortDirString
        ))
      ).subscribe()
    );
    this.importObservable$ = this.rxStompService.watch(`/topic/import/${this.importId}`).pipe(
      map((message: Message) => JSON.parse(message.body)),
      tap((importObject: Import) => this.import = importObject)
    );
    this.subs.push(
      this.importObservable$.pipe(
        switchMap(() => this.getPage(this.pageNumber, this.pageSize, this.sortString, this.sortDirString))
      ).subscribe()
    );
  }

  switchPage(pageEvent: PageEvent): void {
    localStorage.setItem('importViewPageSize', pageEvent.pageSize.toString());
    this.router.navigate(
      [],
      {
        relativeTo: this.route,
        queryParams: {
          page: pageEvent.pageIndex,
          sort: this.sortString,
          dir: this.sortDirString,
          pageSize: pageEvent.pageSize
        }
      }
    );
  }

  sortEvent(sortEvent: Sort): void {
    this.router.navigate(
      [],
      {
        relativeTo: this.route,
        queryParams: {
          page: this.pageNumber,
          sort: this.sortingMap.get(sortEvent.active),
          dir: sortEvent.direction,
          pageSize: this.pageSize
        }
      }
    );
  }

  startImport(): void {
    this.subs.push(
      this.progressSubscription = this.importService.startImport(this.importId).subscribe()
    );
  }

  savePersons(): void {
    this.subs.push(
      this.progressSubscription = this.importService.savePersons(this.importId).subscribe()
    );
  }

  updateIndexNumber(application: Application): void {
    const dialogRef = this.dialog.open(UpdateIndexNumberDialogComponent, {
      width: '300px',
      height: '250px',
      data: {
        personId: application.applicant.usosId,
        indexTypeCode: this.import.indexPoolCode,
        indexNumber: application.applicant.assignedIndexNumber
      }
    });
    this.subs.push(
      dialogRef.afterClosed().subscribe(result => {
        if (result !== undefined && result !== '') {
          application.applicant.assignedIndexNumber = result;
        }
      })
    );
  }

  getElementNumber(application: Application): number {
    return this.page.content.indexOf(application) + this.pageSize * this.page.number + 1;
  }

  onArchiveClick(): void {
    const data = new ConfirmationDialogData('Archiwizuj import', 'Czy na pewno chcesz zarchiwizować import? Procesu nie można odwrócić!');
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data
    });
    this.subs.push(
      dialogRef.afterClosed().pipe(
        filter(result => result === true),
        switchMap((result) => this.importService.archiveImport(this.importId))
      ).subscribe()
    );
  }

  getApplicationEditUrl(application: Application): string {
    return application.editUrl;
  }

  getPersonUsosUrl(application: Application): string {
    return `${this.usosUrl.url}/studenci/programyOsob.jsf?osobaId=${application.applicant.usosId}`;
  }

  ngOnDestroy(): void {
    this.subs.forEach(subscription => subscription.unsubscribe());
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

  getPeselOrIdNumber(application: Application) {
    if (application.applicant.pesel != null) {
      return application.applicant.pesel;
    } else {
      return application.applicant.primaryIdentityDocument.number;
    }
  }

  isStartImportButtonDisabled(): boolean {
    switch (this.import.importStatus) {
      case 'ARCHIVED':
      case 'SAVING':
      case 'STARTED':
      case 'COMPLETE':
      case 'SEARCHING_UIDS':
      case 'SENDING_NOTIFICATIONS':
        return true;
      case 'ERROR':
        return this.import.savedApplicants === this.import.totalCount
          && this.import.totalCount > 0;
      default:
        return false;
    }
  }

  isSavePersonsButtonDisabled(): boolean {
    switch (this.import.importStatus) {
      case 'COMPLETED_WITH_ERRORS':
      case 'IMPORTED':
        return false;
      default:
        return true;
    }
  }

  isArchiveButtonDisabled(): boolean {
    return this.import.importStatus !== 'COMPLETE';
  }

  isFindUidsButtonDisabled(): boolean {
    switch (this.import.importStatus) {
      case 'COMPLETE':
      case 'COMPLETED_WITH_ERRORS':
      case 'ERROR':
        return this.import.savedApplicants !== this.import.totalCount
          || this.import.importedUids === this.import.totalCount;
      default:
        return true;
    }
  }

  onFindUidsClick() {
    this.subs.push(this.progressSubscription = this.importService.findUids(this.importId).subscribe());
  }

  onSendNotificationsClick() {
    this.subs.push(this.progressSubscription = this.importService.sendNotifications(this.importId).subscribe());
  }

  isSendNotificationsDisabled(): boolean {
    switch (this.import.importStatus) {
      case 'COMPLETE':
      case 'COMPLETED_WITH_ERRORS':
        return this.import.savedApplicants !== this.import.totalCount
          || this.import.importedUids !== this.import.totalCount
          || this.import.notificationsSend === this.import.totalCount;
      default:
        return true;
    }
  }

  isCheckForDuplicatesDisabled(): boolean {
    switch (this.import.importStatus) {
      case 'IMPORTED':
        return false;
      default:
        return true;
    }
  }

  onCheckForPotentialDuplicates() {
    this.subs.push(this.progressSubscription = this.importService.checkForPotentialDuplicates(this.importId).subscribe());
  }

  onPotentialDuplicateClick(application: Application) {
    const dialogRef = this.dialog.open(SelectPersonDialogComponent, {
      width: '1200px',
      height: '500px',
      data: {
        application
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result !== undefined && result !== '') {
        this.subs.push(
          this.applicationsService.updatePotentialDuplicateStatus(application.id, {
            usosId: (result.notDuplicate ? undefined : result.person.id),
            potentialDuplicateStatus: (result.notDuplicate ? 'CONFIRMED_NOT_DUPLICATE' : 'OK')
          }).subscribe(updatedApplication => {
            this.import.potentialDuplicates--;
            application.applicant.potentialDuplicateStatus = updatedApplication.applicant.potentialDuplicateStatus;
            application.applicant.usosId = updatedApplication.applicant.usosId;
          })
        );
      }
    });
  }

  getDisplayedColumns(): string[] {
    return [...this.displayedColumns.entries()]
      .filter(column => column[1])
      .map(column => column[0]);
  }

  onColumnCheckboxChange(event: MatCheckboxChange, ...columnIds: string[]) {
    columnIds.forEach(id => this.displayedColumns.set(id, event.checked));
  }
}
