import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';


export interface LoginResponse {
  id: number;
  username: string;
  email: string;
  roles: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth/login'; // backend endpoint
  private readonly ROLE_KEY = 'cg_role';

  constructor(private http: HttpClient) {}

  // Call Spring Security backend
  login(username: string, password: string): Observable<LoginResponse> {
    const body = { username, password };
      return this.http.post<LoginResponse>(this.apiUrl, body, { withCredentials: true }).pipe(
        tap(res => {
      if (res.roles && res.roles.length > 0) {
        localStorage.setItem(this.ROLE_KEY, res.roles[0]); // save first role
      }
  })
);}

  getRole(): string | null {
    return localStorage.getItem(this.ROLE_KEY);
  }

  logout(): void {
    localStorage.removeItem(this.ROLE_KEY);
  }
}


  