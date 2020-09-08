import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UserEditorData} from '../../../model/user/UserEditorData';
import {User} from '../../../model/user/user';

@Component({
  selector: 'app-user-editor',
  templateUrl: './user-editor.component.html',
  styleUrls: ['./user-editor.component.sass']
})
export class UserEditorComponent implements OnInit {

  user: User;

  constructor(
    public dialogRef: MatDialogRef<UserEditorComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserEditorData
  ) {
  }

  ngOnInit(): void {
  }

}
