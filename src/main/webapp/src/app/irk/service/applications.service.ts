import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Page } from '../page'
import {Application} from "../applications/application";

@Injectable({
  providedIn: 'root'
})
export class ApplicationsService {

  private apiUrl = "/api/applications";

  constructor(private http: HttpClient) { }

  getPage() {
    return this.http.get<Page<Application>>(`${this.apiUrl}`)
  }
}
