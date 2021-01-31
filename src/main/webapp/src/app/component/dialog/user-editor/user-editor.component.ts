import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UserEditorData} from '../../../model/user/UserEditorData';
import {User} from '../../../model/user/user';
import {MatSelectionList} from '@angular/material/list';
import {Role} from '../../../model/user/role';
import {UserService} from '../../../service/user-service/user.service';
import {RoleService} from '../../../service/role-service/role.service';

@Component({
  selector: 'app-user-editor',
  templateUrl: './user-editor.component.html',
  styleUrls: ['./user-editor.component.sass']
})
export class UserEditorComponent implements OnInit {

  user: User;
  rolesList: Role[];

  @ViewChild('roleSelectionList') roleSelectionList: MatSelectionList;

  constructor(
    public dialogRef: MatDialogRef<UserEditorComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserEditorData,
    private userService: UserService,
    private roleService: RoleService
  ) {
  }

  ngOnInit(): void {
    this.user = this.data.user;
    this.roleService.getRoles().subscribe(results => {
        this.rolesList = results;
      }// , error => this.onError('Błąd przy pobieraniu ról', error)
    );
  }

  // onError(title: string, error): void {
  //   if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
  //     return;
  //   }
  //   this.dialog.open(ErrorDialogComponent, {
  //     data: new ErrorDialogData(title, error)
  //   });
  // }

  onUpdateUserClick() {
    this.user.roles = this.roleSelectionList.selectedOptions.selected.map(selectedRole => {
      return selectedRole.value;
    });
    this.updateUser(this.user);
  }

  updateUser(user: User) {
    this.userService.update(user).subscribe(
      result => {
        this.user = result;
        this.dialogRef.close(result);
      }// , error => this.onError('Błąd przy aktualizacji użytkownika', error)
    );
  }

  hasRole(role: Role) {
    return this.user.roles.some(element => {
      return element.code === role.code;
    });
  }
}
