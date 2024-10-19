import {Component, OnInit, ViewChild} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {User} from '../../../model/user/user';
import {UserService} from '../../../service/user-service/user.service';
import {Role} from '../../../model/user/role';
import {RoleService} from '../../../service/role-service/role.service';
import {MatListOption, MatSelectionList} from '@angular/material/list';
import {MatFormField, MatLabel} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {NgFor, NgIf} from '@angular/common';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-add-user-dialog',
  templateUrl: './add-user-dialog.component.html',
  styleUrls: ['./add-user-dialog.component.sass'],
  standalone: true,
  imports: [ReactiveFormsModule, MatFormField, MatLabel, MatInput, NgIf, MatSelectionList, NgFor, MatListOption, MatButton, MatIcon]
})
export class AddUserDialogComponent implements OnInit {

  addUserFormGroup: FormGroup<{ uid: FormControl<number | null> }>;
  user: User = new User();
  rolesList: Role[] = [];
  isButtonDisabled = false;

  @ViewChild('roleSelectionList') roleSelectionList: MatSelectionList | null = null;

  constructor(
    public dialogRef: MatDialogRef<AddUserDialogComponent>,
    private userService: UserService,
    private dialog: MatDialog,
    private roleService: RoleService
  ) {
    this.addUserFormGroup = new FormGroup({
      uid: new FormControl<number | null>(null, Validators.required)
    });
  }

  ngOnInit(): void {
    this.roleService.getRoles().subscribe(
      result => {
        this.rolesList = result;
      }
    );
  }

  onSubmit() {
    const selectedRoles = this.roleSelectionList?.selectedOptions.selected.map(selectedOption => {
      return selectedOption.value as Role;
    });
    this.user.uid = this.addUserFormGroup.controls.uid.value;
    if (selectedRoles !== undefined) {
      this.user.roles = selectedRoles;
    }
    this.isButtonDisabled = true;
    this.userService.create(this.user).subscribe({
      next: (user) => {
        this.dialogRef.close(user);
      },
      error: (error) => {
        this.isButtonDisabled = false;
        throw error;
      }
    });
  }
}
