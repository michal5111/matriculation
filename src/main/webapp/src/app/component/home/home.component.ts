import {Component, inject, OnInit} from '@angular/core';
import {map, tap} from 'rxjs/operators';
import {RxStompService} from '@stomp/ng2-stompjs';
import {Message} from '@stomp/stompjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.sass'],
  standalone: true
})
export class HomeComponent implements OnInit {
  private rxStompService = inject(RxStompService);


  ngOnInit() {
    this.rxStompService.watch('/topic/insert/import').pipe(
      map((message: Message) => JSON.parse(message.body)),
      tap(importObj => console.log(importObj))
    ).subscribe();
  }

}
