import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Page} from "../../model/oracle/page/page";
import {Import} from "../../model/import/import";
import {Application} from "../../model/irk/application";

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

  getAvailableRegistrations() {
    return this.http.get<[string]>("api/registrations/codes", httpOptions)
  }

  getAvailableRegistrationProgrammes(programmeCode: String) {
    return this.http.get<[string]>(`api/registrations/codes/${programmeCode}`)
  }

  getAvailableIndexPools() {
    return this.http.get<[string]>("api/indexPool")
  }

  getAvailableStages(programmeCode: String) {
    return this.http.get<[string]>(`api/programme/${programmeCode}/stages`)
  }

  findDidacticCycleCodes(didacticCycleCode: String) {
    return this.http.get<[string]>(`api/didacticCycle/${didacticCycleCode}`)
  }

  createImport(importObject: Import) {
    return this.http.post<Import>(`api/import`, importObject, httpOptions)
  }

  deleteImport(importId: Number) {
    return this.http.delete(`api/import/${importId}`, httpOptions)
  }

  findAllApplicationsByImportId(importId: Number, page: number, size: number, sort?: string, sortDir?: string) {
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
}
