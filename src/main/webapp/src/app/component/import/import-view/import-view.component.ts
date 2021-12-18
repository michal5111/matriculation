import {Component, Inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {ImportService} from '../../../service/import-service/import.service';
import {Page} from '../../../model/oracle/page/page';
import {filter, map, switchMap, tap} from 'rxjs/operators';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatSort, Sort} from '@angular/material/sort';
import {Application} from '../../../model/applications/application';
import {ActivatedRoute, Router} from '@angular/router';
import {ImportProgress} from '../../../model/import/import-progress';
import {Observable, Subscription} from 'rxjs';
import {Import} from '../../../model/import/import';
import {UserService} from '../../../service/user-service/user.service';
import {Document} from '../../../model/applications/document';
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
  importProgress: ImportProgress;
  progressSubscription: Subscription;
  usosUrl: UrlDto;
  pageSize = parseInt(localStorage.getItem('importViewPageSize'), 10) ?? 5;
  pageNumber = 0;
  totalElements = 0;
  page: Page<Application>;
  dataSource = new MatTableDataSource<Application>();
  sortString = 'applicant.name.family';
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
    ['secondarySchoolDocumentNumber', false],
    ['secondarySchoolDocumentIssueInstitution', false],
    ['secondarySchoolDocumentIssueDate', false],
    ['diplomaSchoolDocumentNumber', false],
    ['diplomaSchoolDocumentIssueDateAndPlace', false],
    ['diplomaSchoolDocumentIssueInstitution', false],
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
    ['names', 'applicant.name.family'],
    ['birthDateAndPlace', 'applicant.basicData.dateOfBirth'],
    ['pesel', 'applicant.basicData.pesel'],
    ['indexNumber', 'applicant.assignedIndexNumber'],
    ['applicationImportStatus', 'importStatus'],
    ['importError', 'importError'],
    ['duplicateStatus', 'applicant.potentialDuplicateStatus']
  ]);

  importProgressObservable$: Observable<ImportProgress>;

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
    return this.importService.findAllApplicationsByImportId(this.importId, page, size, sort, sortDir)
      .pipe(
        tap(applicationsPage => {
          this.page = applicationsPage;
          this.totalElements = applicationsPage.totalElements;
        }),
        map(applicationsPage => applicationsPage.content),
        tap(content => this.dataSource.data = content)
      );
  }

  getImport(importId: number): Observable<Import> {
    return this.importService.getImport(importId).pipe(
      tap(importObject => this.import = importObject)
    );
  }

  ngOnInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.importId = this.route.snapshot.params.id;
    if (!this.importId) {
      return;
    }
    this.getImport(this.importId).subscribe();
    this.usosService.getUsosUrl().subscribe(
      result => {
        this.usosUrl = result;
      }
    );
    this.route.queryParams.pipe(
      tap(params => {
        this.pageNumber = +params.page;
        this.sortString = this.sortingMap.get(params.sort);
        this.sortDirString = params.dir;
      }),
      switchMap(() => this.getPage(this.pageNumber, this.pageSize, this.sortString, this.sortDirString))
    ).subscribe();
    this.importProgressObservable$ = this.rxStompService.watch(`/topic/importProgress/${this.importId}`).pipe(
      map((message: Message) => JSON.parse(message.body)),
      tap((importProgress: ImportProgress) => this.importProgress = importProgress)
    );
    this.progressSubscription = this.getPage(this.pageNumber, this.pageSize, this.sortString, this.sortDirString).pipe(
      switchMap(() => this.importService.getImportProgress(this.importId)),
      tap((importProgress: ImportProgress) => this.importProgress = importProgress),
      switchMap(() => this.importProgressObservable$),
      switchMap(() => this.getPage(this.pageNumber, this.pageSize, this.sortString, this.sortDirString))
    ).subscribe();
  }

  switchPage(pageEvent: PageEvent): void {
    // this.pageNumber = pageEvent.pageIndex;
    // this.pageSize = pageEvent.pageSize;
    localStorage.setItem('importViewPageSize', pageEvent.pageSize.toString());
    this.router.navigate(
      [],
      {
        relativeTo: this.route,
        queryParams: {
          page: pageEvent.pageIndex,
          sort: this.route.params,
          dir: this.sortDirString,
          pageSize: pageEvent.pageSize
        }
      }
    );
    // this.getPage(pageEvent.pageIndex, pageEvent.pageSize, this.sortString, this.sortDirString).subscribe();
  }

  sortEvent(sortEvent: Sort): void {
    // this.sortString = this.sortingMap.get(sortEvent.active);
    // this.sortDirString = sortEvent.direction;
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
    // this.getPage(this.pageNumber, this.pageSize, this.sortString, this.sortDirString).subscribe();
  }

  startImport(): void {
    this.progressSubscription = this.importService.startImport(this.importId).subscribe();
  }

  savePersons(): void {
    this.progressSubscription = this.importService.savePersons(this.importId).subscribe();
  }

  getSecondarySchoolDocument(application: Application): Document {
    return application.applicant.educationData.documents
      .find(document => ['D', 'N', 'E', 'Z'].some(code => document.certificateUsosCode === code));
  }

  getDiplomaDocument(application: Application): Document {
    return application.applicant.educationData.documents
      .find(document => ['L', 'I'].some(code => document.certificateUsosCode === code));
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
    dialogRef.afterClosed().subscribe(result => {
      if (result !== undefined && result !== '') {
        application.applicant.assignedIndexNumber = result;
      }
    });
  }

  getElementNumber(application: Application): number {
    return this.page.content.indexOf(application) + this.pageSize * this.page.number + 1;
  }

  onArchiveClick(): void {
    const data = new ConfirmationDialogData('Archiwizuj import', 'Czy na pewno chcesz zarchiwizować import? Procesu nie można odwrócić!');
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data
    });
    dialogRef.afterClosed().pipe(
      filter(result => result === true),
      switchMap((result) => this.importService.archiveImport(this.importId)),
    ).subscribe();
  }

  getApplicationEditUrl(application: Application): string {
    return application.editUrl;
  }

  getPersonUsosUrl(application: Application): string {
    return `${this.usosUrl.url}/studenci/programyOsob.jsf?osobaId=${application.applicant.usosId}`;
  }

  ngOnDestroy(): void {
    this.progressSubscription.unsubscribe();
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
    if (application.applicant.basicData.pesel != null) {
      return application.applicant.basicData.pesel;
    } else {
      return application.applicant.identityDocuments[0].number;
    }
  }

  isStartImportButtonDisabled(): boolean {
    switch (this.importProgress.importStatus) {
      case 'ARCHIVED':
      case 'SAVING':
      case 'STARTED':
      case 'COMPLETE':
      case 'SEARCHING_UIDS':
      case 'SENDING_NOTIFICATIONS':
        return true;
      case 'ERROR':
        return this.importProgress.savedApplicants === this.importProgress.totalCount;
      default:
        return false;
    }
  }

  isSavePersonsButtonDisabled(): boolean {
    switch (this.importProgress.importStatus) {
      case 'COMPLETED_WITH_ERRORS':
      case 'IMPORTED':
        return false;
      default:
        return true;
    }
  }

  isArchiveButtonDisabled(): boolean {
    return this.importProgress.importStatus !== 'COMPLETE';
  }

  isFindUidsButtonDisabled(): boolean {
    switch (this.importProgress.importStatus) {
      case 'COMPLETE':
      case 'COMPLETED_WITH_ERRORS':
      case 'ERROR':
        return this.importProgress.savedApplicants !== this.importProgress.totalCount
          || this.importProgress.importedUids === this.importProgress.totalCount;
      default:
        return true;
    }
  }

  onFindUidsClick() {
    this.progressSubscription = this.importService.findUids(this.importId).subscribe();
  }

  onSendNotificationsClick() {
    this.progressSubscription = this.importService.sendNotifications(this.importId).subscribe();
  }

  isSendNotificationsDisabled(): boolean {
    switch (this.importProgress.importStatus) {
      case 'COMPLETE':
      case 'COMPLETED_WITH_ERRORS':
        return this.importProgress.savedApplicants !== this.importProgress.totalCount
          || this.importProgress.importedUids !== this.importProgress.totalCount
          || this.importProgress.notificationsSend === this.importProgress.totalCount;
      default:
        return true;
    }
  }

  isCheckForDuplicatesDisabled(): boolean {
    switch (this.importProgress.importStatus) {
      case 'IMPORTED':
        return false;
      default:
        return true;
    }
  }

  onCheckForPotentialDuplicates() {
    this.progressSubscription = this.importService.checkForPotentialDuplicates(this.importId).subscribe();
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
        this.applicationsService.updatePotentialDuplicateStatus(application.id, {
          usosId: (result.notDuplicate ? undefined : result.person.id),
          potentialDuplicateStatus: (result.notDuplicate ? 'CONFIRMED_NOT_DUPLICATE' : 'OK')
        }).subscribe(updatedApplication => {
          this.importProgress.potentialDuplicates--;
          application.applicant.potentialDuplicateStatus = updatedApplication.applicant.potentialDuplicateStatus;
          application.applicant.usosId = updatedApplication.applicant.usosId;
        });
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
