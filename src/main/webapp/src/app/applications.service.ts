import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Page } from './page'

@Injectable({
  providedIn: 'root'
})
export class ApplicationsService {

  apiUrl = "/api/applications"

  constructor(private http: HttpClient) { }

  getPage() {
    return this.http.get<Page>(`${this.apiUrl}`)
  }
}
