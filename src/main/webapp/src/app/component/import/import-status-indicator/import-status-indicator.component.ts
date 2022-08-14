import {Component, Input, OnInit} from '@angular/core';
import {ImportStatus} from '../../../model/import/import-status.enum';

@Component({
  selector: 'app-import-status-indicator',
  templateUrl: './import-status-indicator.component.html',
  styleUrls: ['./import-status-indicator.component.sass']
})
export class ImportStatusIndicatorComponent implements OnInit {

  @Input() importStatus: ImportStatus | null = null;
  @Input() tooltip = false;

  constructor() {
  }

  ngOnInit(): void {
  }

}
