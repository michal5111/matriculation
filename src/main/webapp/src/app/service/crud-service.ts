import {Observable} from 'rxjs';
import {Page} from '../model/dto/page/page';
import {BasicService} from './basic-service';

export interface CrudService<T, ID> extends BasicService<T, ID> {
  findAll(page: number, size: number, filers: any, sort?: string, sortDir?: string): Observable<Page<T>>;

  findById(id: ID): Observable<T>;

  update(t: T): Observable<T>;

  create(t: T): Observable<T>;

  delete(id: number): Observable<void>;
}
