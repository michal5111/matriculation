import {CollectionViewer, DataSource} from '@angular/cdk/collections';
import {BehaviorSubject, finalize, map, Observable, tap} from 'rxjs';
import {Page} from '../model/dto/page/page';
import {BasicService} from '../service/basic-service';

export class BasicDataSource<T, F> implements DataSource<T> {
  private elementsSubject = new BehaviorSubject<T[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);

  public loading$ = this.loadingSubject.asObservable();

  public page: Page<T> | null = null;

  constructor(private service: BasicService<T>) {
  }

  connect(collectionViewer: CollectionViewer): Observable<T[]> {
    return this.elementsSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.elementsSubject.complete();
    this.loadingSubject.complete();
  }

  loadElements(pageNumber: number, size: number, filters: F, sort?: string, sortDir?: string) {
    this.loadingSubject.next(true);
    this.service.findAll(pageNumber, size, filters, sort, sortDir).pipe(
      tap(page => this.page = page),
      map(page => page.content),
      finalize(() => this.loadingSubject.next(false))
    ).subscribe(elements => this.elementsSubject.next(elements));
  }
}
