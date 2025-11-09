import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { API_URL } from '../tokens/api-url.tokens';

export interface LoginResponse {
  id: number;
  username: string;
  email: string;
  roles: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = inject(API_URL);  // ✅ dynamic API base URL

  private readonly ROLE_KEY = 'cg_role';

  // ✅ login calls `${API_URL}/auth/login`
  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, { username, password }, { withCredentials: true })
      .pipe(
        tap(res => {
          if (res.roles && res.roles.length > 0) {
            localStorage.setItem(this.ROLE_KEY, res.roles[0]);
          }
        })
      );
  }

  getRole(): string | null {
    return localStorage.getItem(this.ROLE_KEY);
  }

  logout(): void {
    localStorage.removeItem(this.ROLE_KEY);
  }
}
