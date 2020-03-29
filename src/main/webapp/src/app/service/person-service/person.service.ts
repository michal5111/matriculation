import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Person} from "../../model/oracle/Person";
import {Observable} from "rxjs";
import {Page} from "../../model/oracle/page/page";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class PersonService {

  private apiUrl = 'api/persons';

  constructor(private http: HttpClient) { }

  getPersonById(id: Number): Observable<Person> {
    return this.http.get<Person>(`${this.apiUrl}/${id}`, httpOptions)
  }

  getPersons(page: number, size: number, sort?: string, sortDir?: string): Observable<Page<Person>> {
    if (sort && sortDir) {
      return this.http.get<Page<Person>>(`${this.apiUrl}?page=${page}&size=${size}&sort=${sort},${sortDir}`, httpOptions)
    }
    return this.http.get<Page<Person>>(`${this.apiUrl}?page=${page}&size=${size}`, httpOptions)
  }
}
