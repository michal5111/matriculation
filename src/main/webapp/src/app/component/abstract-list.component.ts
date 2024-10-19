import {AfterViewInit, Component, OnDestroy, viewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {BehaviorSubject, debounceTime, distinctUntilChanged, merge, of, Subscription, tap} from 'rxjs';
import {BasicDataSource} from '../dataSource/basic-data-source';

@Component({template: ''})
export abstract class AbstractListComponent<T, ID, F> implements AfterViewInit, OnDestroy {
  protected subs: Subscription[] = [];

  abstract dataSource: BasicDataSource<T, ID, F>;

  abstract displayedColumns: Map<string, boolean>;

  abstract sortingMap: Map<string, string>;

  protected paginator = viewChild<MatPaginator>(MatPaginator);
  protected sort = viewChild<MatSort>(MatSort);

  abstract filtersSubject: BehaviorSubject<F>;

  getElementNumber(t: T): number {
    return (this.dataSource.page?.content.indexOf(t) ?? 0) + (this.dataSource?.page?.size ?? 5) * (this.dataSource?.page?.number ?? 0) + 1;
  }

  getDisplayedColumns(): string[] {
    return [...this.displayedColumns.entries()]
      .filter(column => column[1])
      .map(column => column[0]);
  }

  getPage() {
    this.dataSource.loadElements(
      this.paginator()?.pageIndex ?? 0,
      this.paginator()?.pageSize ?? 5,
      this.filtersSubject.getValue(),
      this.sortingMap.get(this.sort()?.active ?? ''),
      this.sort()?.direction
    );
  }

  ngAfterViewInit(): void {
    if (!this.paginator || !this.sort) {
      throw Error('Paginator or sort is null');
    }
    this.subs.push(
      merge(
        this.paginator()?.page ?? of({}),
        this.sort()?.sortChange ?? of({}),
        this.filtersSubject.pipe(
          debounceTime(300),
          distinctUntilChanged()
        )
      ).pipe(
        tap(() => this.getPage())
      ).subscribe()
    );
  }

  ngOnDestroy(): void {
    this.subs.forEach(sub => sub.unsubscribe());
  }
}
