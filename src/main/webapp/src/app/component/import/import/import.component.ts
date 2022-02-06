import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Page} from '../../../model/oracle/page/page';
import {Import} from '../../../model/import/import';
import {MatTableDataSource} from '@angular/material/table';
import {ImportService} from '../../../service/import-service/import.service';
import {map, switchMap, tap} from 'rxjs/operators';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatSort, Sort} from '@angular/material/sort';
import {MatExpansionPanel} from '@angular/material/expansion';
import {UserService} from '../../../service/user-service/user.service';
import {Subscription, timer} from 'rxjs';

@Component({
  selector: 'app-import',
  templateUrl: './import.component.html',
  styleUrls: ['./import.component.sass']
})
export class ImportComponent implements OnInit, OnDestroy {

  page: Page<Import>;
  totalElements = 0;
  pageIndex = 0;
  pageSize = 0;
  pageSubscription: Subscription;
  dataSource = new MatTableDataSource<Import>();
  sortString = 'id';
  sortDirString = 'desc';
  displayedColumns: string[] = [
    'id',
    'dataSourceType',
    'registration',
    'programmeCode',
    'programmeForeignName',
    'stageCode',
    'didacticCycleCode',
    'indexPoolCode',
    'indexPoolName',
    'startDate',
    'dateOfAddmision',
    'countTotal',
    'countImported',
    'countSaved',
    'status',
    'deleteImport',
    'selectImport'
  ];

  $importProgressObservable = timer(0, 1000).pipe(
    switchMap(() => this.getPage(this.pageIndex, this.pageSize, this.sortString, this.sortDirString)),
  );

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatExpansionPanel, {static: true}) importCreateExpansionPanel: MatExpansionPanel;

  constructor(
    private importService: ImportService,
    public userService: UserService
  ) {
  }

  getPage(page: number, size: number, sort?: string, sortDir?: string) {
    return this.importService.getImportsPage(page, size, sort, sortDir)
      .pipe(
        tap(importPage => {
          this.page = importPage;
          this.totalElements = importPage.totalElements;
          this.pageSize = importPage.size;
        }),
        map(importPage => importPage.content),
        tap(content => this.dataSource.data = content)
      );
  }

  ngOnInit(): void {
    this.dataSource.paginator = this.paginator;
    this.pageSubscription = this.$importProgressObservable.subscribe(
      () => {
      }
    );
  }

  switchPage(pageEvent: PageEvent): void {
    this.pageSize = pageEvent.pageSize;
    this.pageIndex = pageEvent.pageIndex;
    this.getPage(pageEvent.pageIndex, pageEvent.pageSize, this.sortString, this.sortDirString).subscribe(
      () => {
      }
    );
  }

  sortEvent(sortEvent: Sort): void {
    this.sortString = sortEvent.active;
    this.sortDirString = sortEvent.direction;
    this.getPage(this.page.number, this.page.size, this.sortString, this.sortDirString).subscribe(
      () => {
      }
    );
  }

  onImportCreated(event: Import): void {
    // this.importCreateExpansionPanel.close();
    this.getPage(this.page.number, this.page.size, this.sortString, this.sortDirString).subscribe(
      () => {
      }
    );
  }

  onDeleteImportClick(importObj: Import): void {
    this.importService.deleteImport(importObj.id).pipe(
      // switchMap(() => this.getPage(this.page.number, this.page.size, this.sortString, this.sortDirString))
    ).subscribe(
      () => this.dataSource.data = this.dataSource.data.filter(i => i !== importObj)
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
        return importObject.importedApplications > 0;
      default:
        return true;
    }
  }

  ngOnDestroy(): void {
    this.pageSubscription.unsubscribe();
  }
}
