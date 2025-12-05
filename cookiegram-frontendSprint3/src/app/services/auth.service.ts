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

  private readonly ROLE_KEY = 'cg_roles';

  // ✅ login calls `${API_URL}/auth/login`
  login(username: string, password: string) {
  return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, { username, password }, { withCredentials: true })

    .pipe(
      tap(res => {
        if (res.roles) {
          localStorage.setItem(this.ROLE_KEY, JSON.stringify(res.roles));
        }
      })
    );

  }

getRole(): string[] {
  const stored = localStorage.getItem(this.ROLE_KEY);
  if (!stored) return [];
  try {
    return JSON.parse(stored);
  } catch {
    return [];
  }
}



  logout() {
    // call backend to clear cookie/session and remove local role
    return this.http.post(`${this.apiUrl}/auth/logout`, {}, { withCredentials: true, observe: 'response' })
      .pipe(
        tap(() => localStorage.removeItem(this.ROLE_KEY))
      );
  }

  signup(payload: { username: string; email: string; password: string }) {
    return this.http.post(`${this.apiUrl}/auth/signup`, payload, { withCredentials: true, observe: 'response' });
  }
  
  isLoggedIn(): boolean {
    const role = localStorage.getItem(this.ROLE_KEY);
    return !!role;
  }
}
