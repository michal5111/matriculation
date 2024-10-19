import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Application} from '../../model/applications/application';
import {APP_BASE_HREF} from '@angular/common';
import {
  ApplicantUsosIdAndPotentialDuplicateStatusDto
} from '../../model/dto/applicant-usos-id-and-potential-duplicate-status-dto';
import {Observable} from 'rxjs';
import {BasicService} from '../basic-service';
import {Router, UrlSerializer} from '@angular/router';
import {Page} from '../../model/dto/page/page';

@Injectable({
  providedIn: 'root'
})
export class ApplicationsService implements BasicService<Application, number> {
  baseHref = inject(APP_BASE_HREF);
  private http = inject(HttpClient);
  private router = inject(Router);
  private serializer = inject(UrlSerializer);


  private apiUrl = `${this.baseHref}api/applications`;

  findAll(
    page: number,
    size: number,
    filters: any,
    sort?: string | undefined,
    sortDir?: string | undefined
  ): Observable<Page<Application>> {
    let sortString;
    if (sort) {
      sortString = `${sort},${sortDir}`;
    }
    const queryParams = {page, size, sort: sortString, ...filters};
    const tree = this.router.createUrlTree([this.apiUrl], {queryParams});
    return this.http.get<Page<Application>>(this.serializer.serialize(tree));
  }

  findById(id: number): Observable<Application> {
    return this.http.get<Application>(`${this.apiUrl}/${id}`);
  }

  updatePotentialDuplicateStatus(
    applicationId: number,
    potentialDuplicateStatusDto: ApplicantUsosIdAndPotentialDuplicateStatusDto
  ): Observable<Application> {
    return this.http.put<Application>(
      `${this.apiUrl}/${applicationId}/potentialDuplicateStatus`,
      potentialDuplicateStatusDto
    );
  }

  delete(
    applicationId: number
  ): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${applicationId}`);
  }
}
