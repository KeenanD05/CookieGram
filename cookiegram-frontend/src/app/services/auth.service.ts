import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface LoginResponse {
  username: string;
  role: 'CUSTOMER' | 'EMPLOYEE' | 'ADMIN';
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly BASE_URL = 'http://localhost:8080/api/auth';
  private readonly ROLE_KEY = 'cg_role';

  constructor(private http: HttpClient) {}

  // Call Spring Security backend
  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.BASE_URL}/login`, { username, password })
      .pipe(
        tap(res => localStorage.setItem(this.ROLE_KEY, res.role))
      );
  }

  getRole(): string | null {
    return localStorage.getItem(this.ROLE_KEY);
  }

  logout(): void {
    localStorage.removeItem(this.ROLE_KEY);
  }
}
