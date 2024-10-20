import {AfterViewInit, Component, inject} from '@angular/core';
import {merge, of, tap} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {AbstractListComponent} from './abstract-list.component';

@Component({template: ''})
export abstract class AbstractListWithPathParamsComponent<T, ID, F> extends AbstractListComponent<T, ID, F> implements AfterViewInit {
  protected route = inject(ActivatedRoute);
  protected router = inject(Router);


  override ngAfterViewInit() {
    if (!this.paginator() || !this.sort()) {
      throw Error('Paginator or sort is null');
    }
    this.subs.push(
      merge(
        this.paginator()?.page ?? of({}),
        this.sort()?.sortChange ?? of({})
      ).pipe(
        tap(() => this.router.navigate([], {
          queryParams: {
            page: this.paginator()?.pageIndex,
            pageSize: this.paginator()?.pageSize,
            sort: this.sort()?.active
          }
        }))
      ).subscribe(),
      this.filtersSubject.pipe(
        tap(value => {
          this.dataSource.loadElements(
            this.paginator()?.pageIndex ?? 0,
            this.paginator()?.pageSize ?? 5,
            value,
            this.sortingMap.get(this.sort()?.active ?? ''),
            this.sort()?.direction
          );
        })
      ).subscribe(),
      this.route.queryParams.pipe(
        tap(params => {
          this.dataSource.loadElements(
            params['page'] ?? this.paginator()?.pageIndex,
            params['pageSize'] ?? this.paginator()?.pageSize,
            this.filtersSubject.getValue(),
            this.sortingMap.get(params['sort'] ?? this.sort()?.active),
            this.sort()?.direction
          );
        })
      ).subscribe()
    );
  }
}
