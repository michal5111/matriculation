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
    'stageCode',
    'didacticCycleCode',
    'indexPoolCode',
    'startDate',
    'dateOfAddmision',
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
      }// , error => this.onError('Błąd przy pobieraniu strony', error)
    );
  }

  switchPage(pageEvent: PageEvent): void {
    this.pageSize = pageEvent.pageSize;
    this.pageIndex = pageEvent.pageIndex;
    this.getPage(pageEvent.pageIndex, pageEvent.pageSize, this.sortString, this.sortDirString).subscribe(
      () => {
      }// , error => this.onError('Błąd przy pobieraniu strony', error)
    );
  }

  sortEvent(sortEvent: Sort): void {
    this.sortString = sortEvent.active;
    this.sortDirString = sortEvent.direction;
    this.getPage(this.page.number, this.page.size, this.sortString, this.sortDirString).subscribe(
      () => {
      }// , error => this.onError('Błąd przy pobieraniu strony', error)
    );
  }

  onImportCreated(event: Import): void {
    // this.importCreateExpansionPanel.close();
    this.getPage(this.page.number, this.page.size, this.sortString, this.sortDirString).subscribe(
      () => {
      }// , error => this.onError('Błąd przy pobieraniu strony', error)
    );
  }

  onDeleteImportClick(importId: number): void {
    this.importService.deleteImport(importId).pipe(
      switchMap(() => this.getPage(this.page.number, this.page.size, this.sortString, this.sortDirString))
    ).subscribe(
      () => {
      }// , error => this.onError('Błąd przy pobieraniu strony', error)
    );
  }

  // onError(title: string, error): void {
  //   if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
  //     return;
  //   }
  //   if (this.dialog.openDialogs.length > 0) {
  //     return;
  //   }
  //   this.dialog.open(ErrorDialogComponent, {
  //     data: new ErrorDialogData(title, error)
  //   });
  // }

  isDeleteButtonDisabled(importObject: Import): boolean {
    switch (importObject.importProgress.importStatus) {
      case 'STARTED':
      case 'SAVING':
      case 'COMPLETE':
      case 'COMPLETED_WITH_ERRORS':
      case 'ARCHIVED':
        return true;
      case 'IMPORTED':
      case 'ERROR':
      case 'PENDING':
        return importObject.importProgress.importedApplications > 0;
      default:
        return true;
    }
  }

  ngOnDestroy(): void {
    this.pageSubscription.unsubscribe();
  }
}
