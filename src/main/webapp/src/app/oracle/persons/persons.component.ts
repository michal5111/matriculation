import {Component, OnInit, ViewChild} from '@angular/core';
import { Page } from '../page/page';
import {Person} from "../Person";
import {PersonService} from "../service/person.service";
import {MatTableDataSource} from "@angular/material/table";
import {map, tap} from "rxjs/operators";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {Observable} from "rxjs";
import {MatSort, Sort} from "@angular/material/sort";

@Component({
  selector: 'app-persons',
  templateUrl: './persons.component.html',
  styleUrls: ['./persons.component.sass']
})
export class PersonsComponent implements OnInit {

  page: Page<Person>;
  dataSource = new MatTableDataSource<Person>();
  sortString: string = 'surname';
  sortDirString: string = 'asc';
  displayedColumns: string[] = [
    'surname',
    'name',
    'secondName',
    'sex',
    'pesel',
    'birthDate',
    'birthCity',
    'birthCountry',
    'organizationalUnit'
  ];

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  constructor(private personService: PersonService) { }

  getPage(page: number, size: number, sort?: string, sortDir?: string) {
    return this.personService.getPersons(page,size, sort, sortDir)
      .pipe(
        tap(page => this.page = page),
        map(page => page.content),
        tap(results => this.dataSource.data = results)
      )
  }

  ngOnInit() {
    this.dataSource.paginator = this.paginator;
    this.getPage(0,5, this.sortString, this.sortDirString).subscribe()
  }

  switchPage(pageEvent: PageEvent) {
    this.getPage(pageEvent.pageIndex,pageEvent.pageSize, this.sortString, this.sortDirString).subscribe()
  }

  sortEvent(sortEvent: Sort) {
    this.sortString = sortEvent.active;
    this.sortDirString = sortEvent.direction;
    this.getPage(this.page.number,this.page.size, this.sortString, this.sortDirString).subscribe()
  }

}
