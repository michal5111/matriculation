import {Inject, Injectable} from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Page } from '../../model/irk/page'
import {Application} from "../../model/irk/application";
import {APP_BASE_HREF} from "@angular/common";

@Injectable({
  providedIn: 'root'
})
export class ApplicationsService {

  private apiUrl = `${this.baseHref}api/applications`;

  constructor(@Inject(APP_BASE_HREF) public baseHref: string, private http: HttpClient) { }

  getPage() {
    return this.http.get<Page<Application>>(`${this.apiUrl}`)
  }
}
