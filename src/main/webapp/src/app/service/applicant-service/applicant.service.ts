import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Page} from '../../model/applications/page';
import {Applicant} from '../../model/applications/applicant';
import {APP_BASE_HREF} from '@angular/common';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class ApplicantService {

  private apiUrl = `${this.baseHref}api/applicants`;

  constructor(@Inject(APP_BASE_HREF) public baseHref: string, private http: HttpClient) { }

  getApplicantsList() {
    return this.http.get<Page<Applicant>>(this.apiUrl, httpOptions);
  }

  getPhoto(url: string) {
    return this.http.get(url, {responseType: 'blob'});
  }

  getApplicantByPeselAndPrgCode(pesel: string, prgCode: string) {
    return this.http.get<Page<Applicant>>(`${this.apiUrl}/?pesel=${pesel}&prg_code=${prgCode}`);
  }

  getApplicantBySurnameAndPrgCode(surname: string, prgCode: string) {
    return this.http.get<Page<Applicant>>(`${this.apiUrl}/?surname=${surname}&prg_code=${prgCode}`);
  }

  getApplicantByEmailAndPrgCode(email: string, prgCode: string) {
    return this.http.get<Page<Applicant>>(`${this.apiUrl}/?email=${email}&prg_code=${prgCode}`);
  }

  getApplicantByPesel(pesel: string) {
    return this.http.get<Page<Applicant>>(`${this.apiUrl}/?pesel=${pesel}`);
  }

  getApplicantBySurname(surname: string) {
    return this.http.get<Page<Applicant>>(`${this.apiUrl}/?surname=${surname}`);
  }

  getApplicantByEmail(email: string) {
    return this.http.get<Page<Applicant>>(`${this.apiUrl}/?email=${email}`);
  }

  getApplicantByValues(valueType: string, value: string) {
    return this.http.get<Page<Applicant>>(`${this.apiUrl}/?${valueType}=${value}`);
  }

  getApplicantById(id: string) {
    return this.http.get<Applicant>(`${this.apiUrl}/${id}`);
  }

  createImageFromBlob(image: Blob, applicant: Applicant) {
    const reader = new FileReader();
    reader.addEventListener('load', () => {
      applicant.image = reader.result;
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }
}
