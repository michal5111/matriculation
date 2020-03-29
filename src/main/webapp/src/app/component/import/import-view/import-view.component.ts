import {Component, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {ImportService} from "../../../service/import-service/import.service";
import {Page} from "../../../model/oracle/page/page";
import {map, tap} from "rxjs/operators";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {MatSort, Sort} from "@angular/material/sort";
import {Application} from "../../../model/irk/application";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-import-view',
  templateUrl: './import-view.component.html',
  styleUrls: ['./import-view.component.sass']
})
export class ImportViewComponent implements OnInit {

  importId: Number;
  page: Page<Application>;
  dataSource = new MatTableDataSource<Application>();
  sortString: string = 'id';
  sortDirString: string = 'desc';
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

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  constructor(private importService: ImportService, private route: ActivatedRoute) { }

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
    this.getPage(0, 5, this.sortString, this.sortDirString).subscribe()
  }

  switchPage(pageEvent: PageEvent) {
    this.getPage(pageEvent.pageIndex, pageEvent.pageSize, this.sortString, this.sortDirString).subscribe()
  }

  sortEvent(sortEvent: Sort) {
    this.sortString = sortEvent.active;
    this.sortDirString = sortEvent.direction;
    this.getPage(this.page.number, this.page.size, this.sortString, this.sortDirString).subscribe()
  }

  startImport() {
    this.importService.startImport(this.importId).subscribe()
  }

  savePersons() {
    this.importService.savePersons(this.importId).subscribe()
  }
}
