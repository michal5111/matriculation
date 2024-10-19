import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {Page} from '../../../model/dto/page/page';
import {Import} from '../../../model/import/import';
import {ImportService} from '../../../service/import-service/import.service';
import {map, tap} from 'rxjs/operators';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort, MatSortHeader} from '@angular/material/sort';
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelContent,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from '@angular/material/expansion';
import {UserService} from '../../../service/user-service/user.service';
import {BehaviorSubject, Observable} from 'rxjs';
import {Message} from '@stomp/stompjs';
import {RxStompService} from '@stomp/ng2-stompjs';
import {BasicDataSource} from '../../../dataSource/basic-data-source';
import {AbstractListWithPathParamsComponent} from '../../abstract-list-with-path-params.component';
import {RouterLink} from '@angular/router';
import {ImportSetupComponent} from '../import-setup/import-setup.component';
import {DatePipe} from '@angular/common';
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
import {ImportStatusIndicatorComponent} from '../import-status-indicator/import-status-indicator.component';
import {MatAnchor, MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-import',
  templateUrl: './import.component.html',
  styleUrls: ['./import.component.sass'],
  standalone: true,
  imports: [
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatExpansionPanelContent,
    ImportSetupComponent,
    MatTable,
    MatSort,
    MatColumnDef,
    MatHeaderCellDef,
    MatHeaderCell,
    MatSortHeader,
    MatCellDef,
    MatCell,
    ImportStatusIndicatorComponent,
    MatButton,
    MatIcon,
    MatAnchor,
    RouterLink,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRowDef,
    MatRow,
    MatPaginator,
    DatePipe
  ]
})
export class ImportComponent extends AbstractListWithPathParamsComponent<Import, number, {}> implements OnInit, OnDestroy {
  private importService = inject(ImportService);
  userService = inject(UserService);
  private rxStompService = inject(RxStompService);


  page: Page<Import> | null = null;
  dataSource: BasicDataSource<Import, number, {}>;
  filtersSubject: BehaviorSubject<{}>;
  sortingMap: Map<string, string> = new Map<string, string>([
    ['id', 'id'],
    ['dataSourceType', 'dataSourceName'],
    ['registration', 'registration'],
    ['programmeCode', 'programmeCode'],
    ['programmeForeignName', 'programmeForeignName'],
    ['stageCode', 'stageCode'],
    ['didacticCycleCode', 'didacticCycleCode'],
    ['indexPoolName', 'indexPoolName'],
    ['startDate', 'startDate'],
    ['dateOfAddmision', 'dateOfAddmision'],
    ['countTotal', 'countTotal'],
    ['countImported', 'countImported'],
    ['countSaved', 'countSaved'],
    ['status', 'status']
  ]);
  displayedColumns: Map<string, boolean> = new Map<string, boolean>([
    ['id', true],
    ['dataSourceType', true],
    ['registration', true],
    ['programmeCode', true],
    ['programmeForeignName', true],
    ['stageCode', true],
    ['didacticCycleCode', true],
    ['indexPoolName', true],
    ['startDate', true],
    ['dateOfAddmision', true],
    ['countTotal', true],
    ['countImported', true],
    ['countSaved', true],
    ['status', true],
    ['deleteImport', true],
    ['selectImport', true]
  ]);

  constructor() {
    super();
    const importService = this.importService;

    this.filtersSubject = new BehaviorSubject<{}>({});
    this.dataSource = new BasicDataSource<Import, number, {}>(importService);
  }

  ngOnInit(): void {
    this.subs.push(
      this.rxStompService.watch('/topic/import').pipe(
        map((message: Message) => JSON.parse(message.body)),
        tap((importObject: Import) => {
          const comparator = (oldElement: Import, newElement: Import) => {
            return oldElement.id === newElement.id;
          };
          this.dataSource.updateElement(importObject, comparator);
        })
      ).subscribe(),
      this.rxStompService.watch('/topic/delete/import').pipe(
        map((message: Message) => JSON.parse(message.body)),
        tap(importObj => this.removeImportFromData(importObj))
      ).subscribe(),
      this.rxStompService.watch('/topic/insert/import').pipe(
        map((message: Message) => JSON.parse(message.body)),
        tap(() => this.getPage())
      ).subscribe()
    );

  }

  onImportCreated(event: Import): void {
    this.getPage();
  }

  deleteImport(importObject: Import): Observable<any> {
    if (importObject.id == null) {
      throw Error('Import id is null');
    }
    return this.importService.delete(importObject.id).pipe(
      tap(() => this.removeImportFromData(importObject))
    );
  }

  removeImportFromData(importObject: Import) {
    this.dataSource.filterElements(i => i.id !== importObject.id);
  }

  onDeleteImportClick(importObj: Import): void {
    if (importObj.id == null) {
      throw Error('Import id is null');
    }
    this.subs.push(
      this.importService.delete(importObj.id).subscribe()
    );
  }

  isDeleteButtonDisabled(importObject: Import): boolean {
    switch (importObject.importStatus) {
      case 'STARTED':
      case 'SAVING':
      case 'COMPLETE':
      case 'COMPLETED_WITH_ERRORS':
      case 'ARCHIVED':
        return true;
      case 'IMPORTED':
      case 'ERROR':
      case 'PENDING':
        return importObject.savedApplicants > 0;
      default:
        return true;
    }
  }

  override ngOnDestroy(): void {
    super.ngOnDestroy();
    this.subs.forEach(subscription => subscription.unsubscribe());
  }
}
