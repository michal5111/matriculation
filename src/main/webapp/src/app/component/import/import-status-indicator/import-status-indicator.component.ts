import {Component, Input, OnInit} from '@angular/core';
import {ImportStatus} from '../../../model/import/import-status.enum';

import {MatIcon} from '@angular/material/icon';
import {MatTooltip} from '@angular/material/tooltip';

@Component({
  selector: 'app-import-status-indicator',
  templateUrl: './import-status-indicator.component.html',
  styleUrls: ['./import-status-indicator.component.sass'],
  standalone: true,
  imports: [MatIcon, MatTooltip]
})
export class ImportStatusIndicatorComponent implements OnInit {

  @Input() importStatus: ImportStatus | null = null;
  @Input() tooltip = false;

  constructor() {
  }

  ngOnInit(): void {
  }

}
