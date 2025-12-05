import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
@Injectable({ providedIn: 'root' })
export class OrdersService {
  private apiUrl = 'http://localhost:8080/api/customer/orders';

  constructor(private http: HttpClient) {}

  getMyOrders(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { withCredentials: true });
  }
}
