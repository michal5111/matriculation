import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-progress-viewer',
  templateUrl: './progress-viewer.component.html',
  styleUrls: ['./progress-viewer.component.sass']
})
export class ProgressViewerComponent implements OnInit {

  @Input() value = 0;
  @Input() total: number | null = null;
  @Input() header: string | undefined;
  @Input() errorsCount = 0;

  constructor() {
  }

  ngOnInit(): void {
  }

  calculatePercentage(): number {
    if (this.total == null) {
      return 0;
    }
    return (this.value + this.errorsCount) * 100 / this.total;
  }

}
