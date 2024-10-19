import {Component, computed, input} from '@angular/core';
import {MatProgressBar} from '@angular/material/progress-bar';

@Component({
  selector: 'app-progress-viewer',
  templateUrl: './progress-viewer.component.html',
  styleUrls: ['./progress-viewer.component.sass'],
  standalone: true,
  imports: [MatProgressBar]
})
export class ProgressViewerComponent {

  value = input(0);
  total = input<number | null>(null);
  header = input<string>();
  errorsCount = input(0);

  percentage = computed(() => (this.value() + this.errorsCount()) * 100 / (this.total() ?? 0));
}
