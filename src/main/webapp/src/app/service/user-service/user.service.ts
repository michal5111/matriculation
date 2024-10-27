import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {UserDetails} from '../../model/user/userDetails';
import {filter, tap} from 'rxjs/operators';
import {APP_BASE_HREF} from '@angular/common';
import {User} from '../../model/user/user';
import {BehaviorSubject, map, Observable} from 'rxjs';
import {Page} from '../../model/dto/page/page';
import {ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, RouterStateSnapshot} from '@angular/router';
import {nonNull} from '../../util/util';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class UserService implements CanActivate {
  baseHref = inject(APP_BASE_HREF);
  private http = inject(HttpClient);


  apiUrl = `${this.baseHref}api/users`;
  userUrl = `${this.baseHref}api/user`;
  private userSubject: BehaviorSubject<UserDetails | null> = new BehaviorSubject<UserDetails | null>(null);

  $user: Observable<UserDetails> = this.userSubject.asObservable().pipe(
    filter(nonNull)
  );

  setUser(user: UserDetails | null) {
    this.userSubject.next(user);
  }

  isAuthenticated() {
    return this.userSubject.value != null;
  }

  setUnauthenticated() {
    this.setUser(null);
  }

  findById(userId: number) {
    return this.http.get<User>(`${this.apiUrl}/${userId}`, httpOptions);
  }

  update(user: User): Observable<User> {
    return this.http.put<User>(this.apiUrl, user, httpOptions);
  }

  create(user: User): Observable<User> {
    return this.http.post<User>(this.apiUrl, user, httpOptions);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getAll(page: number, size: number, sort?: string, sortDir?: string): Observable<Page<User>> {
    if (sort && sortDir) {
      return this.http.get<Page<User>>(`${this.apiUrl}?page=${page}&size=${size}&sort=${sort},${sortDir}`, httpOptions);
    }
    return this.http.get<Page<User>>(`${this.apiUrl}?page=${page}&size=${size}`, httpOptions);
  }

  hasRole(role: string): boolean {
    if (this.userSubject.value === undefined || this.userSubject.value?.authorities === undefined) {
      return false;
    }
    return this.userSubject.value.authorities.find(x => x.authority === role) !== undefined;
  }

  hasAnyRole(...roles: string[]): boolean {
    return roles.some(role => this.hasRole(role));
  }

  init$() {
    return this.http.get<UserDetails>(this.userUrl, httpOptions).pipe(
      tap(user => {
        if (Object.keys(user).length !== 0) {
          this.setUser(user);
        }
      })
    );
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): MaybeAsync<GuardResult> {
    const authorities: string[] = route.data?.['authorities'];
    if (!authorities || authorities.length === 0) {
      return true;
    } else {
      return this.$user.pipe(
        map(user => user.authorities),
        map(() => this.hasAnyRole(...authorities))
      );
    }
  }
}
