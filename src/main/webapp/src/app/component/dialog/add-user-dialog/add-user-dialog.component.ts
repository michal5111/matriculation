import {Component, OnInit, ViewChild} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {User} from '../../../model/user/user';
import {UserService} from '../../../service/user-service/user.service';
import {Role} from '../../../model/user/role';
import {RoleService} from '../../../service/role-service/role.service';
import {MatSelectionList} from '@angular/material/list';

@Component({
  selector: 'app-add-user-dialog',
  templateUrl: './add-user-dialog.component.html',
  styleUrls: ['./add-user-dialog.component.sass']
})
export class AddUserDialogComponent implements OnInit {

  addUserFormGroup: FormGroup;
  user: User;
  rolesList: Role[];

  @ViewChild('roleSelectionList') roleSelectionList: MatSelectionList;

  constructor(
    public dialogRef: MatDialogRef<AddUserDialogComponent>,
    private formBuilder: FormBuilder,
    private userService: UserService,
    private dialog: MatDialog,
    private roleService: RoleService
  ) {
  }

  ngOnInit(): void {
    this.addUserFormGroup = this.formBuilder.group({
      uid: ['', Validators.required]
    });
    this.roleService.getRoles().subscribe(
      result => {
        this.rolesList = result;
      }// , error => this.onError('Błąd przy pobieraniu listy ról', error)
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

  onSubmit() {
    this.user = new User();
    this.user.uid = this.addUserFormGroup.value.uid;
    this.user.roles = this.roleSelectionList.selectedOptions.selected.map(selectedOption => {
      return selectedOption.value;
    });
    console.log(this.user);
    this.userService.create(this.user).subscribe(
      user => {
        this.dialogRef.close(user);
      }// , error => this.onError('Błąd dodawania użytkownika', error)
    );
  }
}
