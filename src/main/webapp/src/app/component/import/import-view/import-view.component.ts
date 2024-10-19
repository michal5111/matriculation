import {Component, inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ImportService} from '../../../service/import-service/import.service';
import {filter, map, switchMap, tap} from 'rxjs/operators';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort, MatSortHeader} from '@angular/material/sort';
import {Application} from '../../../model/applications/application';
import {BehaviorSubject, merge, Observable, Subscription} from 'rxjs';
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
import {MatCheckbox, MatCheckboxChange} from '@angular/material/checkbox';
import {RxStompService} from '@stomp/ng2-stompjs';
import {Message} from '@stomp/stompjs';
import {WS_URL} from '../../../injectableTokens/WS_URL';
import {ImportEditorComponent} from '../../dialog/import-editor/import-editor.component';
import {AbstractListWithPathParamsComponent} from '../../abstract-list-with-path-params.component';
import {BasicDataSource} from 'src/app/dataSource/basic-data-source';
import {DatePipe} from '@angular/common';
import {MatCard, MatCardActions, MatCardContent, MatCardSubtitle, MatCardTitle} from '@angular/material/card';
import {MatAnchor, MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {ImportStatusIndicatorComponent} from '../import-status-indicator/import-status-indicator.component';
import {ProgressViewerComponent} from '../../progress-viewer/progress-viewer.component';
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
import {MatTooltip} from '@angular/material/tooltip';

type filterType = { importId: number };

@Component({
  selector: 'app-import-view',
  templateUrl: './import-view.component.html',
  styleUrls: ['./import-view.component.sass'],
  standalone: true,
  imports: [
    MatCard,
    MatCardTitle,
    MatCardSubtitle,
    MatCardActions,
    MatButton,
    MatIcon,
    MatCardContent,
    ImportStatusIndicatorComponent,
    ProgressViewerComponent,
    MatCheckbox,
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
    MatHeaderRowDef,
    MatHeaderRow,
    MatRowDef,
    MatRow,
    MatPaginator,
    DatePipe
  ]
})
export class ImportViewComponent extends AbstractListWithPathParamsComponent<Application, number, filterType> implements OnInit, OnDestroy {
  private importService = inject(ImportService);
  private usosService = inject(UsosService);
  userService = inject(UserService);
  private dialog = inject(MatDialog);
  private applicationsService = inject(ApplicationsService);
  rxStompService = inject(RxStompService);
  private wsUrl = inject(WS_URL);

  dataSource: BasicDataSource<Application, number, filterType>;
  filtersSubject: BehaviorSubject<filterType>;
  importId = -1;
  import: Import | null = null;
  progressSubscription: Subscription | null = null;
  usosUrl: UrlDto | null = null;
  // override pageSize = parseInt(localStorage.getItem('importViewPageSize') ?? '5', 10);
  // page: Page<Application> | null = null;
  // override sortString = 'applicant.family';
  // override sortDirString = 'asc';
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
    ['warnings', true],
    ['delete', true]
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

  public import$: Observable<Import>;
  @ViewChild(MatPaginator) override paginator: MatPaginator | null = null;
  @ViewChild(MatSort) override sort: MatSort | null = null;

  constructor() {
    super();
    const applicationsService = this.applicationsService;

    this.importId = this.route.snapshot.params['id'];
    this.filtersSubject = new BehaviorSubject<filterType>({
      importId: this.importId
    });
    this.dataSource = new BasicDataSource<Application, number, filterType>(applicationsService);
    this.import$ = merge(
      this.importService.findById(this.importId),
      this.rxStompService.watch(`/topic/import/${this.importId}`).pipe(
        map((message: Message) => JSON.parse(message.body))
      )
    ).pipe(
      tap((importObject: Import) => this.import = importObject),
      // distinctUntilChanged((previous, current) => previous.importStatus === current.importStatus),
      tap(() => this.getPage())
    );
  }

  ngOnInit(): void {
    this.subs.push(
      this.import$.subscribe(),
      this.usosService.getUsosUrl().subscribe(
        result => this.usosUrl = result
      )
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
        indexTypeCode: this.import?.indexPoolCode,
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

  onArchiveClick(): void {
    const data = new ConfirmationDialogData('Archiwizuj import', 'Czy na pewno chcesz zarchiwizować import? Procesu nie można odwrócić!');
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data
    });
    this.subs.push(
      dialogRef.afterClosed().pipe(
        filter(result => result === true),
        switchMap(() => this.importService.archiveImport(this.importId))
      ).subscribe()
    );
  }

  getPersonUsosUrl(application: Application): string {
    return `${this.usosUrl?.url}/studenci/programyOsob.jsf?osobaId=${application.applicant.usosId}`;
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

  showImportErrorDialog(importObject: Import): void {
    if (importObject.importStatus === 'ERROR') {
      if (this.dialog.openDialogs.length > 0) {
        return;
      }
      this.dialog.open(ErrorDialogComponent, {
        data: new ErrorDialogData('Błąd przy importowaniu', importObject.error, importObject.stackTrace)
      });
    }
  }

  getPeselOrIdNumber(application: Application) {
    if (application.applicant.pesel != null) {
      return application.applicant.pesel;
    } else {
      return application.applicant.primaryIdentityDocument?.number;
    }
  }

  isStartImportButtonDisabled(): boolean {
    switch (this.import?.importStatus) {
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
    if (this.import?.potentialDuplicates != null && this.import?.potentialDuplicates > 0) {
      return true;
    }
    switch (this.import?.importStatus) {
      case 'COMPLETED_WITH_ERRORS':
      case 'IMPORTED':
        return false;
      default:
        return true;
    }
  }

  isArchiveButtonDisabled(): boolean {
    return this.import?.importStatus !== 'COMPLETE';
  }

  isFindUidsButtonDisabled(): boolean {
    switch (this.import?.importStatus) {
      case 'COMPLETE':
      case 'COMPLETED_WITH_ERRORS':
      case 'ERROR':
        return false;
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
    switch (this.import?.importStatus) {
      case 'COMPLETE':
      case 'COMPLETED_WITH_ERRORS':
        return this.import.notificationsSend === this.import.totalCount;
      default:
        return true;
    }
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
            application.applicant.potentialDuplicateStatus = updatedApplication.applicant.potentialDuplicateStatus;
            application.applicant.usosId = updatedApplication.applicant.usosId;
          })
        );
      }
    });
  }

  onColumnCheckboxChange(event: MatCheckboxChange, ...columnIds: string[]) {
    columnIds.forEach(id => this.displayedColumns.set(id, event.checked));
  }

  deleteApplication(id: number) {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: new ConfirmationDialogData('Jesteś pewien?', 'Czy na pewno chcesz usunąć osobę?')
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.applicationsService.delete(id).subscribe({
          next: () => {
            this.dataSource.filterElements(application => application.id !== id);
          }
        });
      }
    });
  }

  openEditorDialog() {
    this.dialog.open(ImportEditorComponent, {
      data: {
        import: this.import
      }
    });
  }

  isEditButtonDisabled(): boolean {
    return this.import?.importStatus != null ? !(
      this.import?.importStatus === 'PENDING'
      || this.import?.importStatus === 'IMPORTED'
      || this.import?.importStatus === 'COMPLETED_WITH_ERRORS'
      || (this.import?.importStatus === 'ERROR' && this.import.totalCount !== this.import.savedApplicants)
    ) : true;
  }

  isDeleteButtonDisabled(application: Application): boolean {
    if (application.importStatus === 'IMPORTED') {
      return true;
    }
    return this.isStartImportButtonDisabled();
  }
}
