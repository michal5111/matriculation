import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {ImportService} from "../../../service/import-service/import.service";
import {Page} from "../../../model/oracle/page/page";
import {flatMap, map, tap} from "rxjs/operators";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {MatSort, Sort} from "@angular/material/sort";
import {Application} from "../../../model/irk/application";
import {ActivatedRoute} from "@angular/router";
import {ImportProgress} from "../../../model/import/import-progress";
import {Subscription, timer} from "rxjs";
import {Import} from "../../../model/import/import";
import {ImportStatus} from "../../../model/import/import-status.enum";

@Component({
  selector: 'app-import-view',
  templateUrl: './import-view.component.html',
  styleUrls: ['./import-view.component.sass']
})
export class ImportViewComponent implements OnInit, OnDestroy {

  importId: Number;
  import: Import;
  importProgress: ImportProgress;
  progressSubscription: Subscription;
  page: Page<Application>;
  dataSource = new MatTableDataSource<Application>();
  sortString: string = 'id';
  sortDirString: string = 'asc';
  displayedColumns: string[] = [
    'id',
    'irkId',
    'email',
    'nameGiven',
    'nameMiddle',
    'nameFamily',
    'applicationImportStatus',
    'importError'
  ];
  $importProgressObservable = timer(0, 1000).pipe(
    flatMap(() => this.importService.getImportProgress(this.importId)),
    tap(result => this.importProgress = result),
    flatMap(() => this.getPage(this.page.number, this.page.size, this.sortString, this.sortDirString)),
    tap(result => {
      console.log("la");
      if (this.importProgress.importStatus == "IMPORTED"
        || this.importProgress.importStatus == "COMPLETE"
        || this.importProgress.importStatus == "PENDING") {
        console.log("unsubscribe");
        this.progressSubscription.unsubscribe()
      }
    })
  );

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  constructor(private importService: ImportService, private route: ActivatedRoute) {
  }

  getPage(page: number, size: number, sort?: string, sortDir?: string) {
    return this.importService.findAllApplicationsByImportId(this.importId, page, size, sort, sortDir)
      .pipe(
        tap(page => this.page = page),
        map(page => page.content),
        tap(results => this.dataSource.data = results)
      )
  }

  ngOnInit(): void {
    this.dataSource.paginator = this.paginator;
    this.importId = this.route.snapshot.params.id;
    if (!this.importId) {
      return
    }
    this.getPage(0, 15, this.sortString, this.sortDirString).subscribe();
    this.progressSubscription = this.$importProgressObservable.subscribe()
  }

  switchPage(pageEvent: PageEvent) {
    this.getPage(pageEvent.pageIndex, pageEvent.pageSize, this.sortString, this.sortDirString).subscribe();
  }

  sortEvent(sortEvent: Sort) {
    this.sortString = sortEvent.active;
    this.sortDirString = sortEvent.direction;
    this.getPage(this.page.number, this.page.size, this.sortString, this.sortDirString).subscribe();
  }

  startImport() {
    this.progressSubscription.unsubscribe();
    this.progressSubscription = this.importService.startImport(this.importId).pipe(
      flatMap(() => this.$importProgressObservable)
    ).subscribe();
  }


  savePersons() {
    this.progressSubscription.unsubscribe();
    this.progressSubscription = this.importService.savePersons(this.importId).pipe(
      flatMap(() => this.$importProgressObservable)
    ).subscribe();
  }

  ngOnDestroy(): void {
    this.progressSubscription.unsubscribe()
  }
}
