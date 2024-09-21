import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Page} from '../../model/dto/page/page';
import {Import} from '../../model/import/import';
import {Application} from '../../model/applications/application';
import {Registration} from '../../model/applications/registration';
import {APP_BASE_HREF} from '@angular/common';
import {DataSource} from '../../model/import/dataSource';
import {Programme} from '../../model/applications/programme';
import {Person} from '../../model/oracle/Person';
import {CrudService} from '../crud-service';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})

export class ImportService implements CrudService<Import, number> {

  private apiUrl = `${this.baseHref}api`;

  constructor(@Inject(APP_BASE_HREF) public baseHref: string, private http: HttpClient) {
  }

  findAll(page: number, size: number, filers: any, sort?: string | undefined, sortDir?: string | undefined): Observable<Page<Import>> {
    if (sort && sortDir) {
      return this.http.get<Page<Import>>(`${this.apiUrl}/import?page=${page}&size=${size}&sort=${sort},${sortDir}`, httpOptions);
    }
    return this.http.get<Page<Import>>(`${this.apiUrl}/import?page=${page}&size=${size}`, httpOptions);
  }

  getAvailableRegistrations(dataSourceType: string, filter?: string): Observable<Registration[]> {
    if (filter) {
      return this.http.get<[Registration]>(`${this.apiUrl}/dataSources/${dataSourceType}/registrations?filter=${filter}`, httpOptions);
    }
    return this.http.get<[Registration]>(`${this.apiUrl}/dataSources/${dataSourceType}/registrations`, httpOptions);
  }

  getAvailableRegistrationProgrammes(registrationCode: string, dataSourceType: string): Observable<[Programme]> {
    return this.http.get<[Programme]>(`${this.apiUrl}/dataSources/${dataSourceType}/registrations/${encodeURIComponent(registrationCode)}/programmes`);
  }

  create(importObject: Import): Observable<Import> {
    return this.http.post<Import>(`${this.apiUrl}/import`, importObject, httpOptions);
  }

  update(importObject: Import): Observable<Import> {
    return this.http.put<Import>(`${this.apiUrl}/import`, importObject, httpOptions);
  }

  delete(importId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/import/${importId}`, httpOptions);
  }

  findById(importId: number) {
    return this.http.get<Import>(`${this.apiUrl}/import/${importId}`, httpOptions);
  }

  findAllApplicationsByImportId(
    importId: number,
    page: number,
    size: number,
    sort?: string,
    sortDir?: string
  ): Observable<Page<Application>> {
    if (sort && sortDir) {
      return this.http.get<Page<Application>>(`${this.apiUrl}/import/${importId}/applications?page=${page}&size=${size}&sort=${sort},${sortDir}`, httpOptions);
    }
    return this.http.get<Page<Application>>(`${this.apiUrl}/import/${importId}/applications?page=${page}&size=${size}`, httpOptions);
  }

  startImport(importId: number) {
    return this.http.put(`${this.apiUrl}/import/${importId}`, null, httpOptions);
  }

  savePersons(importId: number) {
    return this.http.get(`${this.apiUrl}/import/${importId}/save`);
  }

  archiveImport(importId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/import/${importId}/archive`, null, httpOptions);
  }

  getAvailableDataSources(): Observable<[DataSource]> {
    return this.http.get<[DataSource]>(`${this.apiUrl}/dataSources`);
  }

  findUids(importId: number): Observable<void> {
    return this.http.get<void>(`${this.apiUrl}/import/${importId}/importUids`);
  }

  sendNotifications(importId: number): Observable<void> {
    return this.http.get<void>(`${this.apiUrl}/import/${importId}/notifications`);
  }

  getPotentialDuplicates(applicantId: number): Observable<Array<Person>> {
    return this.http.get<[Person]>(`${this.apiUrl}/applicant/${applicantId}/potentialDuplicates`);
  }
}
