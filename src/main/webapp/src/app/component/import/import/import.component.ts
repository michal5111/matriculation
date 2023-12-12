import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Page} from '../../../model/dto/page/page';
import {Import} from '../../../model/import/import';
import {ImportService} from '../../../service/import-service/import.service';
import {map, tap} from 'rxjs/operators';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatExpansionPanel} from '@angular/material/expansion';
import {UserService} from '../../../service/user-service/user.service';
import {BehaviorSubject, Observable} from 'rxjs';
import {Message} from '@stomp/stompjs';
import {RxStompService} from '@stomp/ng2-stompjs';
import {BasicDataSource} from '../../../dataSource/basic-data-source';
import {AbstractListWithPathParamsComponent} from '../../abstract-list-with-path-params.component';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-import',
  templateUrl: './import.component.html',
  styleUrls: ['./import.component.sass']
})
export class ImportComponent extends AbstractListWithPathParamsComponent<Import, number, {}> implements OnInit, OnDestroy {

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

  @ViewChild(MatPaginator) override paginator: MatPaginator | null = null;
  @ViewChild(MatSort) override sort: MatSort | null = null;
  @ViewChild(MatExpansionPanel) importCreateExpansionPanel: MatExpansionPanel | null = null;

  constructor(
    private importService: ImportService,
    public userService: UserService,
    protected override route: ActivatedRoute,
    protected override router: Router,
    private rxStompService: RxStompService
  ) {
    super(route, router);
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
