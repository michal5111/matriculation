import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Page} from "../../model/oracle/page/page";
import {Import} from "../../model/import/import";
import {Application} from "../../model/irk/application";
import {ImportProgress} from "../../model/import/import-progress";
import {IndexType} from "../../model/oracle/index-type";
import {Registration} from "../../model/irk/registration";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})

export class ImportService {

  private apiUrl = "api/import";

  constructor(private http: HttpClient) { }

  getImportsPage(page: number, size: number, sort?: string, sortDir?: string): Observable<Page<Import>> {
    if (sort && sortDir) {
      return this.http.get<Page<Import>>(`${this.apiUrl}?page=${page}&size=${size}&sort=${sort},${sortDir}`, httpOptions)
    }
    return this.http.get<Page<Import>>(`${this.apiUrl}?page=${page}&size=${size}`, httpOptions)
  }

  getAvailableRegistrations(): Observable<[Registration]> {
    return this.http.get<[Registration]>("api/registrations/codes", httpOptions)
  }

  getAvailableRegistrationProgrammes(programmeCode: String): Observable<[string]> {
    return this.http.get<[string]>(`api/registrations/codes/${programmeCode}`)
  }

  getAvailableIndexPools(): Observable<[IndexType]> {
    return this.http.get<[IndexType]>("api/indexPool")
  }

  getAvailableStages(programmeCode: String): Observable<[string]> {
    return this.http.get<[string]>(`api/programme/${programmeCode}/stages`)
  }

  findDidacticCycleCodes(didacticCycleCode: String): Observable<[string]> {
    return this.http.get<[string]>(`api/didacticCycle/${didacticCycleCode}`)
  }

  createImport(importObject: Import): Observable<Import> {
    return this.http.post<Import>(`api/import`, importObject, httpOptions)
  }

  deleteImport(importId: Number) {
    return this.http.delete(`api/import/${importId}`, httpOptions)
  }

  getImport(importId: Number) {
    return this.http.get<Import>(`api/import/${importId}`, httpOptions)
  }

  findAllApplicationsByImportId(importId: Number, page: number, size: number, sort?: string, sortDir?: string): Observable<Page<Application>> {
    if (sort && sortDir) {
      return this.http.get<Page<Application>>(`api/import/${importId}/applications?page=${page}&size=${size}&sort=${sort},${sortDir}`, httpOptions)
    }
    return this.http.get<Page<Application>>(`api/import/${importId}/applications?page=${page}&size=${size}`, httpOptions)
  }

  startImport(importId: Number) {
    return this.http.put(`api/import/${importId}`, null, httpOptions)
  }

  savePersons(importId: Number) {
    return this.http.get(`api/import/save/${importId}`)
  }

  getImportProgress(importId: Number): Observable<ImportProgress> {
    return this.http.get<ImportProgress>(`api/import/progress/${importId}`)
  }
}
