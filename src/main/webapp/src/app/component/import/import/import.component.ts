import {Component, OnInit, ViewChild} from '@angular/core';
import {Page} from "../../../model/oracle/page/page";
import {Import} from "../../../model/import/import";
import {MatTableDataSource} from "@angular/material/table";
import {ImportService} from "../../../service/import-service/import.service";
import {flatMap, map, tap} from "rxjs/operators";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {MatSort, Sort} from "@angular/material/sort";
import {MatExpansionPanel} from "@angular/material/expansion";
import {UserService} from "../../../service/user-service/user.service";

@Component({
  selector: 'app-import',
  templateUrl: './import.component.html',
  styleUrls: ['./import.component.sass']
})
export class ImportComponent implements OnInit {

  page: Page<Import>;
  dataSource = new MatTableDataSource<Import>();
  sortString: string = 'id';
  sortDirString: string = 'desc';
  displayedColumns: string[] = [
    'id',
    'registration',
    'programmeCode',
    'stageCode',
    'didacticCycleCode',
    'indexPoolCode',
    'startDate',
    'dateOfAddmision',
    'deleteImport',
    'selectImport'
  ];

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatExpansionPanel, {static: true}) importCreateExpansionPanel: MatExpansionPanel

  constructor(private importService: ImportService, public _userService: UserService) { }

  getPage(page: number, size: number, sort?: string, sortDir?: string) {
    return this.importService.getImportsPage(page, size, sort, sortDir)
      .pipe(
        tap(page => this.page = page),
        map(page => page.content),
        tap(results => this.dataSource.data = results)
      )
  }

  ngOnInit(): void {
    this.dataSource.paginator = this.paginator;
    this.getPage(0, 15, this.sortString, this.sortDirString).subscribe()
  }

  switchPage(pageEvent: PageEvent) {
    this.getPage(pageEvent.pageIndex, pageEvent.pageSize, this.sortString, this.sortDirString).subscribe()
  }

  sortEvent(sortEvent: Sort) {
    this.sortString = sortEvent.active;
    this.sortDirString = sortEvent.direction;
    this.getPage(this.page.number, this.page.size, this.sortString, this.sortDirString).subscribe()
  }

  onImportCreated(event: Import) {
    this.importCreateExpansionPanel.close()
    this.getPage(this.page.number, this.page.size, this.sortString, this.sortDirString).subscribe()
  }

  onDeleteImportClick(importId: Number) {
    this.importService.deleteImport(importId).pipe(
     flatMap(() => this.getPage(this.page.number, this.page.size, this.sortString, this.sortDirString))
    ).subscribe();
  }
}
