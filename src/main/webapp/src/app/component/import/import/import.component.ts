import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Page} from '../../../model/dto/page/page';
import {Import} from '../../../model/import/import';
import {MatTableDataSource} from '@angular/material/table';
import {ImportService} from '../../../service/import-service/import.service';
import {map, switchMap, tap} from 'rxjs/operators';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatSort, Sort} from '@angular/material/sort';
import {MatExpansionPanel} from '@angular/material/expansion';
import {UserService} from '../../../service/user-service/user.service';
import {Observable, Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {Message} from '@stomp/stompjs';
import {RxStompService} from '@stomp/ng2-stompjs';

@Component({
  selector: 'app-import',
  templateUrl: './import.component.html',
  styleUrls: ['./import.component.sass']
})
export class ImportComponent implements OnInit, OnDestroy {

  private subs: Subscription[] = [];
  page: Page<Import> | null = null;
  totalElements = 0;
  pageNumber = 0;
  pageSize = parseInt(localStorage.getItem('importPageSize') ?? '10', 10);
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

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator | null = null;
  @ViewChild(MatSort, {static: true}) sort: MatSort | null = null;
  @ViewChild(MatExpansionPanel, {static: true}) importCreateExpansionPanel: MatExpansionPanel | null = null;

  constructor(
    private importService: ImportService,
    public userService: UserService,
    private route: ActivatedRoute,
    private router: Router,
    private rxStompService: RxStompService
  ) {
  }

  getPage(page: number, size: number, sort?: string, sortDir?: string) {
    return this.importService.getImportsPage(page, size, sort, sortDir).pipe(
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
    this.subs.push(
      this.route.queryParams.pipe(
        tap(params => {
          this.pageNumber = params['page'] ?? this.pageNumber;
          this.sortString = params['sort'] ?? this.sortString;
          this.sortDirString = params['dir'] ?? this.sortDirString;
          this.pageSize = params['pageSize'] ?? this.pageSize;
        }),
        switchMap(params => this.getPage(
          params['page'] ?? this.pageNumber,
          this.pageSize,
          this.sortString,
          this.sortDirString
        ))
      ).subscribe(),
      this.rxStompService.watch('/topic/import').pipe(
        map((message: Message) => JSON.parse(message.body)),
        tap((importObject: Import) => {
          this.dataSource.data = this.dataSource.data.map(element => {
            if (element.id === importObject.id) {
              return importObject;
            }
            return element;
          });
        })
      ).subscribe(),
      this.rxStompService.watch('/topic/delete/import').pipe(
        map((message: Message) => JSON.parse(message.body)),
        tap(importObj => this.removeImportFromData(importObj))
      ).subscribe(),
      this.rxStompService.watch('/topic/insert/import').pipe(
        map((message: Message) => JSON.parse(message.body)),
        switchMap(() => this.getPage(this.page?.number ?? 0, this.page?.size ?? 10, this.sortString, this.sortDirString))
      ).subscribe()
    );

  }

  switchPage(pageEvent: PageEvent): void {
    localStorage.setItem('importPageSize', pageEvent.pageSize.toString());
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
          sort: sortEvent.active,
          dir: sortEvent.direction,
          pageSize: this.pageSize
        }
      }
    );
  }

  onImportCreated(event: Import): void {
    this.subs.push(
      this.getPage(this.page?.number ?? 0, this.page?.size ?? 10, this.sortString, this.sortDirString).subscribe()
    );
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
    this.dataSource.data = this.dataSource.data.filter(i => i.id !== importObject.id);
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

  ngOnDestroy(): void {
    this.subs.forEach(subscription => subscription.unsubscribe());
  }
}
