import {Component, OnInit, ViewChild} from '@angular/core';
import {UserService} from '../../service/user-service/user.service';
import {User} from '../../model/user/user';
import {MatTableDataSource} from '@angular/material/table';
import {map, tap} from 'rxjs/operators';
import {Page} from '../../model/dto/page/page';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatSort, Sort} from '@angular/material/sort';
import {MatDialog} from '@angular/material/dialog';
import {AddUserDialogComponent} from '../dialog/add-user-dialog/add-user-dialog.component';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ConfirmationDialogData} from '../../model/dialog/confirmation-dialog-data';
import {ConfirmationDialogComponent} from '../dialog/confirmation-dialog/confirmation-dialog.component';
import {UserEditorComponent} from '../dialog/user-editor/user-editor.component';
import {UserEditorData} from '../../model/user/UserEditorData';

@Component({
  selector: 'app-user-manager',
  templateUrl: './user-manager.component.html',
  styleUrls: ['./user-manager.component.sass']
})
export class UserManagerComponent implements OnInit {

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator | null = null;
  @ViewChild(MatSort, {static: true}) sort: MatSort | null = null;

  pageSize = 5;
  pageNumber = 0;
  totalElements = 0;
  page: Page<User> | undefined;
  sortString = 'id';
  sortDirString = 'asc';

  displayedColumns: string[] = [
    'id',
    'uid',
    'givenName',
    'surname',
    'email',
    'edit',
    'delete'
  ];

  dataSource = new MatTableDataSource<User>();

  constructor(
    private userService: UserService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {
  }

  ngOnInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.getUserPage(this.pageNumber, this.pageSize, this.sortString, this.sortDirString).subscribe(
      () => {
      }
    );
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

  switchPage(pageEvent: PageEvent): void {
    this.pageNumber = pageEvent.pageIndex;
    this.pageSize = pageEvent.pageSize;
    this.getUserPage(pageEvent.pageIndex, pageEvent.pageSize, this.sortString, this.sortDirString).subscribe(
      () => {
      }
    );
  }

  sortEvent(sortEvent: Sort): void {
    this.sortDirString = sortEvent.direction;
    this.getUserPage(this.pageNumber, this.pageSize, this.sortString, sortEvent.active).subscribe(
      () => {
      }
    );
  }

  onAddUserClick() {
    const dialogRef = this.dialog.open(AddUserDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      if (result !== undefined && result !== '') {
        this.dataSource.data = [...this.dataSource.data, result];
        const snackbarRef = this.snackBar.open('Użytkownik dodany', 'OK', {
          duration: 3000
        });
        snackbarRef.onAction().subscribe(() => snackbarRef.dismiss());
      }
    });
  }

  onDeleteUserClick(user: User) {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: new ConfirmationDialogData('Jesteś pewien?', 'Czy na pewno chcesz usunąć użytkownika?')
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteUser(user);
      }
    });
  }

  deleteUser(user: User) {
    if (user.id === null) {
      throw Error('User id is null');
    }
    this.userService.delete(user.id).subscribe(
      () => {
        this.dataSource.data = this.dataSource.data.filter(element => {
          return element !== user;
        });
      }
    );
  }

  onEditUserClick(user: User) {
    const dialogRef = this.dialog.open(UserEditorComponent, {
      data: new UserEditorData(user)
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result !== undefined && result !== '') {

      }
    });
  }
}
