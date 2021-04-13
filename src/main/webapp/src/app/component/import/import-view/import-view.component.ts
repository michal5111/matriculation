import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {ImportService} from '../../../service/import-service/import.service';
import {Page} from '../../../model/oracle/page/page';
import {filter, map, switchMap, takeWhile, tap} from 'rxjs/operators';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatSort, Sort} from '@angular/material/sort';
import {Application} from '../../../model/applications/application';
import {ActivatedRoute} from '@angular/router';
import {ImportProgress} from '../../../model/import/import-progress';
import {Observable, Subscription, timer} from 'rxjs';
import {Import} from '../../../model/import/import';
import {UserService} from '../../../service/user-service/user.service';
import {Document} from '../../../model/applications/document';
import {MatDialog} from '@angular/material/dialog';
import {UpdateIndexNumberDialogComponent} from '../../dialog/update-index-number-dialog/update-index-number-dialog.component';
import {ConfirmationDialogComponent} from '../../dialog/confirmation-dialog/confirmation-dialog.component';
import {ConfirmationDialogData} from '../../../model/dialog/confirmation-dialog-data';
import {ErrorDialogComponent} from '../../dialog/error-dialog/error-dialog.component';
import {ErrorDialogData} from '../../../model/dialog/error-dialog-data';
import {UrlDto} from '../../../model/import/urlDto';
import {UsosService} from '../../../service/usos-service/usos.service';

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
  pageSize = 5;
  pageNumber = 0;
  totalElements = 0;
  page: Page<Application>;
  dataSource = new MatTableDataSource<Application>();
  sortString = 'id';
  sortDirString = 'asc';
  displayedColumns: string[] = [
    'lp',
    'id',
    'foreignId',
    'usosId',
    'uid',
    'names',
    'birthDateAndPlace',
    'pesel',
    'secondarySchoolDocumentNumber',
    'secondarySchoolDocumentIssueInstitution',
    'secondarySchoolDocumentIssueDate',
    'diplomaSchoolDocumentNumber',
    'diplomaSchoolDocumentIssueDateAndPlace',
    'diplomaSchoolDocumentIssueInstitution',
    'indexNumber',
    'applicationImportStatus',
    'importError'
  ];
  sortingMap: Map<string, string> = new Map<string, string>([
    ['id', 'id'],
    ['foreignId', 'foreignId'],
    ['usosId', 'applicant.usosId'],
    ['names', 'applicant.name.family'],
    ['birthDateAndPlace', 'applicant.basicData.dateOfBirth'],
    ['pesel', 'applicant.basicData.pesel'],
    ['indexNumber', 'applicant.assignedIndexNumber'],
    ['applicationImportStatus', 'importStatus'],
    ['importError', 'importError']
  ]);

  workingStatuses = [
    'STARTED',
    'SAVING',
    'SEARCHING_UIDS',
    'SENDING_NOTIFICATIONS'
  ];

  $importProgressObservable = timer(0, 1000).pipe(
    switchMap(() => this.importService.getImportProgress(this.importId)),
    tap(result => this.importProgress = result),
    switchMap(() => this.getPage(this.pageNumber, this.pageSize, this.sortString, this.sortDirString)),
    takeWhile(() => this.workingStatuses.some(status => status === this.importProgress.importStatus))
  );

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  constructor(
    private importService: ImportService,
    private usosService: UsosService,
    private route: ActivatedRoute,
    public userService: UserService,
    private dialog: MatDialog
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
    this.progressSubscription = this.getPage(this.pageNumber, this.pageSize, this.sortString, this.sortDirString).pipe(
      switchMap(() => this.$importProgressObservable)
    ).subscribe();
  }

  switchPage(pageEvent: PageEvent): void {
    this.pageNumber = pageEvent.pageIndex;
    this.pageSize = pageEvent.pageSize;
    this.getPage(pageEvent.pageIndex, pageEvent.pageSize, this.sortString, this.sortDirString).subscribe();
  }

  sortEvent(sortEvent: Sort): void {
    this.sortString = this.sortingMap.get(sortEvent.active);
    this.sortDirString = sortEvent.direction;
    this.getPage(this.pageNumber, this.pageSize, this.sortString, this.sortDirString).subscribe();
  }

  startImport(): void {
    this.progressSubscription.unsubscribe();
    this.progressSubscription = this.importService.startImport(this.importId).pipe(
      switchMap(() => this.$importProgressObservable)
    ).subscribe();
  }

  savePersons(): void {
    this.progressSubscription.unsubscribe();
    this.progressSubscription = this.importService.savePersons(this.importId).pipe(
      switchMap(() => this.$importProgressObservable)
    ).subscribe();
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
      switchMap(() => this.importService.getImportProgress(this.importId)),
      switchMap(() => this.getPage(this.pageNumber, this.pageSize, this.sortString, this.sortDirString))
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
    this.progressSubscription.unsubscribe();
    this.progressSubscription = this.importService.findUids(this.importId).pipe(
      switchMap(() => this.$importProgressObservable)
    ).subscribe();
  }

  onSendNotificationsClick() {
    this.progressSubscription.unsubscribe();
    this.progressSubscription = this.importService.sendNotifications(this.importId).pipe(
      switchMap(() => this.$importProgressObservable)
    ).subscribe();
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
}
