import { Component, OnInit, ViewChild, inject } from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UserEditorData} from '../../../model/user/UserEditorData';
import {User} from '../../../model/user/user';
import {MatListOption, MatSelectionList} from '@angular/material/list';
import {Role} from '../../../model/user/role';
import {UserService} from '../../../service/user-service/user.service';
import {RoleService} from '../../../service/role-service/role.service';
import {tap} from 'rxjs/operators';

import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-user-editor',
  templateUrl: './user-editor.component.html',
  styleUrls: ['./user-editor.component.sass'],
  standalone: true,
  imports: [MatSelectionList, MatListOption, MatButton, MatIcon]
})
export class UserEditorComponent implements OnInit {
  dialogRef = inject<MatDialogRef<UserEditorComponent>>(MatDialogRef);
  data = inject<UserEditorData>(MAT_DIALOG_DATA);
  private userService = inject(UserService);
  private roleService = inject(RoleService);


  user: User = new User();
  rolesList: Role[] = [];

  @ViewChild('roleSelectionList') roleSelectionList: MatSelectionList | null = null;

  ngOnInit(): void {
    this.userService.findById(this.data.user.id ?? -1).pipe(
      tap(user => console.log(user))
    ).subscribe(user => this.user = user);
    this.roleService.getRoles().subscribe(results => {
        this.rolesList = results;
      }
    );
  }

  onUpdateUserClick() {
    const roles = this.roleSelectionList?.selectedOptions.selected.map(selectedRole => {
      return selectedRole.value as Role;
    });
    if (roles != null) {
      this.user.roles = roles;
    }
    this.updateUser(this.user);
  }

  updateUser(user: User) {
    this.userService.update(user).subscribe(
      result => {
        this.user = result;
        this.dialogRef.close(result);
      }
    );
  }

  hasRole(role: Role) {
    return this.user.roles?.some(element => {
      return element.code === role.code;
    });
  }
}
