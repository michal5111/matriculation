import {Component, Input, OnInit} from '@angular/core';
import {ImportStatus} from '../../../model/import/import-status.enum';
import {NgIf, NgSwitch, NgSwitchCase} from '@angular/common';
import {MatIcon} from '@angular/material/icon';
import {MatTooltip} from '@angular/material/tooltip';

@Component({
  selector: 'app-import-status-indicator',
  templateUrl: './import-status-indicator.component.html',
  styleUrls: ['./import-status-indicator.component.sass'],
  standalone: true,
  imports: [NgSwitch, NgSwitchCase, MatIcon, MatTooltip, NgIf]
})
export class ImportStatusIndicatorComponent implements OnInit {

  @Input() importStatus: ImportStatus | null = null;
  @Input() tooltip = false;

  constructor() {
  }

  ngOnInit(): void {
  }

}
