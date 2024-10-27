import {ChangeDetectionStrategy, Component, inject, input, OnDestroy} from '@angular/core';
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
import {ImportEditorComponent} from '../../dialog/import-editor/import-editor.component';
import {AbstractListWithPathParamsComponent} from '../../abstract-list-with-path-params.component';
import {BasicDataSource} from 'src/app/dataSource/basic-data-source';
import {AsyncPipe, DatePipe} from '@angular/common';
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
import {toObservable} from '@angular/core/rxjs-interop';
import {nonNull} from '../../../util/util';

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
    DatePipe,
    AsyncPipe
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ImportViewComponent extends AbstractListWithPathParamsComponent<Application, number, filterType> implements OnDestroy {
  private readonly importService = inject(ImportService);
  private readonly usosService = inject(UsosService);
  protected readonly userService = inject(UserService);
  private readonly dialog = inject(MatDialog);
  private readonly applicationsService = inject(ApplicationsService);
  protected readonly rxStompService = inject(RxStompService);

  dataSource: BasicDataSource<Application, number, filterType> =
    new BasicDataSource<Application, number, filterType>(this.applicationsService);
  importId = input<number>();
  filtersSubject: BehaviorSubject<filterType> = new BehaviorSubject<filterType>({
    importId: -1
  });
  importId$ = toObservable(this.importId).pipe(
    filter(nonNull),
    tap(importId => this.filtersSubject.next({importId}))
  );

  progressSubscription: Subscription | null = null;
  usosUrl$ = this.usosService.getUsosUrl();
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

  public import$: Observable<Import> = merge(
    this.importId$.pipe(
      filter(nonNull),
      switchMap(id => this.importService.findById(id))
    ),
    this.importId$.pipe(
      filter(nonNull),
      switchMap(id => this.rxStompService.watch(`/topic/import/${id}`)),
      map((message: Message) => JSON.parse(message.body))
    )
  ).pipe(
    tap(() => this.getPage())
  );

  startImport(): void {
    this.subs.push(
      this.progressSubscription = this.importId$.pipe(
        filter(nonNull),
        switchMap(id => this.importService.startImport(id))
      ).subscribe()
    );
  }

  savePersons(): void {
    this.subs.push(
      this.progressSubscription = this.importId$.pipe(
        filter(nonNull),
        switchMap(id => this.importService.savePersons(id))
      ).subscribe()
    );
  }

  updateIndexNumber(application: Application, importObj: Import): void {
    const dialogRef = this.dialog.open(UpdateIndexNumberDialogComponent, {
      width: '300px',
      height: '250px',
      data: {
        personId: application.applicant.usosId,
        indexTypeCode: importObj?.indexPoolCode,
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
        switchMap(() => this.importId$),
        filter(nonNull),
        switchMap(importId => this.importService.archiveImport(importId))
      ).subscribe()
    );
  }

  getPersonUsosUrl(application: Application, usosUrl: UrlDto): string {
    return `${usosUrl?.url}/studenci/programyOsob.jsf?osobaId=${application.applicant.usosId}`;
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

  isStartImportButtonDisabled(importObj: Import): boolean {
    switch (importObj?.importStatus) {
      case 'ARCHIVED':
      case 'SAVING':
      case 'STARTED':
      case 'COMPLETE':
      case 'SEARCHING_UIDS':
      case 'SENDING_NOTIFICATIONS':
        return true;
      case 'ERROR':
        return importObj.savedApplicants === importObj.totalCount
          && importObj.totalCount > 0;
      default:
        return false;
    }
  }

  isSavePersonsButtonDisabled(importObj: Import): boolean {
    if (importObj?.potentialDuplicates != null && importObj?.potentialDuplicates > 0) {
      return true;
    }
    switch (importObj?.importStatus) {
      case 'COMPLETED_WITH_ERRORS':
      case 'IMPORTED':
        return false;
      default:
        return true;
    }
  }

  isArchiveButtonDisabled(importObj: Import): boolean {
    return importObj?.importStatus !== 'COMPLETE';
  }

  isFindUidsButtonDisabled(importObj: Import): boolean {
    switch (importObj?.importStatus) {
      case 'COMPLETE':
      case 'COMPLETED_WITH_ERRORS':
      case 'ERROR':
        return false;
      default:
        return true;
    }
  }

  onFindUidsClick(importId: number) {
    this.subs.push(this.progressSubscription = this.importService.findUids(importId).subscribe());
  }

  onSendNotificationsClick(importId: number) {
    this.subs.push(this.progressSubscription = this.importService.sendNotifications(importId).subscribe());
  }

  isSendNotificationsDisabled(importObj: Import): boolean {
    switch (importObj?.importStatus) {
      case 'COMPLETE':
      case 'COMPLETED_WITH_ERRORS':
        return importObj.notificationsSend === importObj.totalCount;
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
            usosId: result.person?.id,
            potentialDuplicateStatus: result.person?.id ? 'CONFIRMED_DUPLICATE' : 'CONFIRMED_NOT_DUPLICATE'
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

  openEditorDialog(importObj: Import) {
    this.dialog.open(ImportEditorComponent, {
      data: {
        import: importObj
      }
    });
  }

  isEditButtonDisabled(importObj: Import): boolean {
    return importObj?.importStatus != null ? !(
      importObj?.importStatus === 'PENDING'
      || importObj?.importStatus === 'IMPORTED'
      || importObj?.importStatus === 'COMPLETED_WITH_ERRORS'
      || (importObj?.importStatus === 'ERROR' && importObj.totalCount !== importObj.savedApplicants)
    ) : true;
  }

  isDeleteButtonDisabled(application: Application, importObj: Import): boolean {
    if (application.importStatus === 'IMPORTED') {
      return true;
    }
    return this.isStartImportButtonDisabled(importObj);
  }
}
