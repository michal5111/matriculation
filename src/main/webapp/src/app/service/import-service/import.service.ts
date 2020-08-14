import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Page} from '../../model/oracle/page/page';
import {Import} from '../../model/import/import';
import {Application} from '../../model/applications/application';
import {ImportProgress} from '../../model/import/import-progress';
import {Registration} from '../../model/applications/registration';
import {APP_BASE_HREF} from '@angular/common';
import {DataSource} from "../../model/import/dataSource";
import {Programme} from "../../model/applications/programme";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})

export class ImportService {

  private apiUrl = `${this.baseHref}api`;

  constructor(@Inject(APP_BASE_HREF) public baseHref: string, private http: HttpClient) {
  }

  getImportsPage(page: number, size: number, sort?: string, sortDir?: string): Observable<Page<Import>> {
    if (sort && sortDir) {
      return this.http.get<Page<Import>>(`${this.apiUrl}/import?page=${page}&size=${size}&sort=${sort},${sortDir}`, httpOptions);
    }
    return this.http.get<Page<Import>>(`${this.apiUrl}/import?page=${page}&size=${size}`, httpOptions);
  }

  getAvailableRegistrations(dataSourceType: string): Observable<[Registration]> {
    return this.http.get<[Registration]>(`${this.apiUrl}/registrations/${dataSourceType}/codes`, httpOptions);
  }

  getAvailableRegistrationProgrammes(registrationCode: string, dataSourceType: string): Observable<[Programme]> {
    return this.http.get<[Programme]>(`${this.apiUrl}/registrations/${dataSourceType}/codes/${encodeURIComponent(registrationCode)}`);
  }

  createImport(importObject: Import): Observable<Import> {
    return this.http.post<Import>(`${this.apiUrl}/import`, importObject, httpOptions);
  }

  deleteImport(importId: number) {
    return this.http.delete(`${this.apiUrl}/import/${importId}`, httpOptions);
  }

  getImport(importId: number) {
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

  getImportProgress(importId: number): Observable<ImportProgress> {
    return this.http.get<ImportProgress>(`${this.apiUrl}/import/${importId}/progress`);
  }

  archiveImport(importId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/import/${importId}/archive`, null, httpOptions);
  }

  getAvailableDataSources(): Observable<[DataSource]> {
    return this.http.get<[DataSource]>(`${this.apiUrl}/import/dataSources`);
  }
}
