import {Component, OnInit, ViewChild} from '@angular/core';
import {UserService} from '../../service/user-service/user.service';
import {User} from '../../model/user/user';
import {MatTableDataSource} from '@angular/material/table';
import {map, tap} from 'rxjs/operators';
import {Page} from '../../model/oracle/page/page';
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {MatSort, Sort} from "@angular/material/sort";
import {HttpErrorResponse} from "@angular/common/http";
import {ErrorDialogComponent} from "../dialog/error-dialog/error-dialog.component";
import {ErrorDialogData} from "../../model/dialog/error-dialog-data";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-user-manager',
  templateUrl: './user-manager.component.html',
  styleUrls: ['./user-manager.component.sass']
})
export class UserManagerComponent implements OnInit {

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  pageSize = 5;
  pageNumber = 0;
  totalElements = 0;
  page: Page<User>;
  sortString = 'id';
  sortDirString = 'asc';

  displayedColumns: string[] = [
    'id',
    'uid',
    'edit'
  ];

  dataSource = new MatTableDataSource<User>();

  constructor(
    private userService: UserService,
    private dialog: MatDialog
  ) {
  }

  ngOnInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.getUserPage(this.pageNumber, this.pageSize, this.sortString, this.sortDirString).subscribe(
      () => {
      },
      error => this.onError('Błąd przy pobieraniu użytkowników', error));
  }

  create(user: User) {
    return this.userService.create(user);
  }

  update(user: User) {
    return this.userService.update(user);
  }

  delete(id: number) {
    return this.userService.delete(id);
  }

  getUserPage(page: number, size: number, sort?: string, sortDir?: string) {
    return this.userService.getAll(page, size, sort, sortDir).pipe(
      tap(userPage => {
        this.page = userPage;
        this.totalElements = userPage.totalElements;
      }),
      map(userPage => userPage.content),
      tap(content => this.dataSource.data = content)
    );
  }

  onError(title: string, error): void {
    if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
      return;
    }
    if (this.dialog.openDialogs.length > 0) {
      return;
    }
    this.dialog.open(ErrorDialogComponent, {
      data: new ErrorDialogData(title, error)
    });
  }

  switchPage(pageEvent: PageEvent): void {
    this.pageNumber = pageEvent.pageIndex;
    this.pageSize = pageEvent.pageSize;
    this.getUserPage(pageEvent.pageIndex, pageEvent.pageSize, this.sortString, this.sortDirString).subscribe(
      () => {
      },
      error => this.onError('Błąd przy pobieraniu strony', error)
    );
  }

  sortEvent(sortEvent: Sort): void {
    this.sortDirString = sortEvent.direction;
    this.getUserPage(this.pageNumber, this.pageSize, this.sortString, sortEvent.active).subscribe(
      () => {
      },
      error => this.onError('Błąd przy pobieraniu strony', error)
    );
  }

}
