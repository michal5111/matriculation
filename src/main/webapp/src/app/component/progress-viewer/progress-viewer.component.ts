import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-progress-viewer',
  templateUrl: './progress-viewer.component.html',
  styleUrls: ['./progress-viewer.component.sass']
})
export class ProgressViewerComponent implements OnInit {

  @Input() value: number;
  @Input() total: number;
  @Input() header: string;
  @Input() errorsCount = 0;

  constructor() {
  }

  ngOnInit(): void {
  }

  calculatePercentage(): number {
    return (this.value + this.errorsCount) * 100 / this.total;
  }

}
