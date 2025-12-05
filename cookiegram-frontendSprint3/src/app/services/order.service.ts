import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/staff/orders';

  getOrders(page: number, size: number): Observable<any> {
    return this.http.get(
      `${this.baseUrl}?page=${page}&size=${size}`,
      { withCredentials: true }   // ⭐ REQUIRED FOR AUTH COOKIE
    );
  }

  getOrderById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`, {
      withCredentials: true
    });
  }

  updateStatus(id: number, status: string): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/${id}`,
      null,
      {
        params: { orderStatus: status },
        withCredentials: true
      }
    );
  }
}
