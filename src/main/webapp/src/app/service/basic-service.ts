import {Observable} from 'rxjs';
import {Page} from '../model/dto/page/page';

export interface BasicService<T, ID> {
  findAll(page: number, size: number, filters: any, sort?: string, sortDir?: string): Observable<Page<T>>;

  findById(id: ID): Observable<T>;
}
