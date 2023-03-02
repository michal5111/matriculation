import {CollectionViewer, DataSource} from '@angular/cdk/collections';
import {BehaviorSubject, finalize, map, Observable, tap} from 'rxjs';
import {BasicService} from '../service/basic-service';
import {Page} from '../model/dto/page/page';

export class BasicDataSource<T, ID, F> implements DataSource<T> {
  private elementsSubject = new BehaviorSubject<T[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);

  public loading$ = this.loadingSubject.asObservable();

  public page: Page<T> | null = null;

  constructor(private service: BasicService<T, ID>) {
  }

  connect(collectionViewer: CollectionViewer): Observable<T[]> {
    return this.elementsSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.elementsSubject.complete();
    this.loadingSubject.complete();
  }

  updateElement(newElement: T, comparator: (element: T, newElement: T) => boolean) {
    const newList = this.elementsSubject.getValue().map(oldElement => {
      if (comparator(oldElement, newElement)) {
        return newElement;
      }
      return oldElement;
    });
    this.elementsSubject.next(newList);
  }

  filterElements(comparator: (element: T, index: number, array: T[]) => boolean) {
    const newList = this.elementsSubject.getValue().filter(comparator);
    this.elementsSubject.next(newList);
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
