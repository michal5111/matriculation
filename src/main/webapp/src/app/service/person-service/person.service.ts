import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Person} from '../../model/oracle/Person';
import {Observable} from 'rxjs';
import {Page} from '../../model/dto/page/page';
import {APP_BASE_HREF} from '@angular/common';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class PersonService {
  baseHref = inject(APP_BASE_HREF);
  private http = inject(HttpClient);


  private apiUrl = `${this.baseHref}api/person`;

  getPersonById(id: number): Observable<Person> {
    return this.http.get<Person>(`${this.apiUrl}/${id}`, httpOptions);
  }

  getPersons(page: number, size: number, sort?: string, sortDir?: string): Observable<Page<Person>> {
    if (sort && sortDir) {
      return this.http.get<Page<Person>>(`${this.apiUrl}?page=${page}&size=${size}&sort=${sort},${sortDir}`, httpOptions);
    }
    return this.http.get<Page<Person>>(`${this.apiUrl}?page=${page}&size=${size}`, httpOptions);
  }
}
