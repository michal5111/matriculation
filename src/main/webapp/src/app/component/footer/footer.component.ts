import {Component} from '@angular/core';
import {MatDivider} from '@angular/material/divider';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.sass'],
  standalone: true,
  imports: [MatDivider]
})
export class FooterComponent {
}
