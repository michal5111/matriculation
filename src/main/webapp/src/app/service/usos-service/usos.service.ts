import {Inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {APP_BASE_HREF} from '@angular/common';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {UrlDto} from '../../model/import/urlDto';
import {IndexType} from '../../model/oracle/index-type';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class UsosService {

  private apiUrl = `${this.baseHref}api/usos`;

  constructor(@Inject(APP_BASE_HREF) public baseHref: string, private http: HttpClient) {
  }

  updateIndexNumberByUsosIdAndIndexType(personId: number, indexTypeCode: string, indexNumber: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/person/${personId}/indexNumber?indexType=${indexTypeCode}&indexNumber=${indexNumber}`, null, httpOptions);
  }

  getUsosUrl(): Observable<UrlDto> {
    return this.http.get<UrlDto>(`${this.apiUrl}/url`);
  }

  getAvailableIndexPools(): Observable<[IndexType]> {
    return this.http.get<[IndexType]>(`${this.apiUrl}/indexPool`);
  }

  getAvailableStages(programmeCode: string): Observable<[string]> {
    return this.http.get<[string]>(`${this.apiUrl}/programme/${encodeURIComponent(programmeCode)}/stages`);
  }

  findDidacticCycleCodes(didacticCycleCode: string): Observable<[string]> {
    return this.http.get<[string]>(`${this.apiUrl}/didacticCycle?code=${encodeURIComponent(didacticCycleCode)}`);
  }
}
