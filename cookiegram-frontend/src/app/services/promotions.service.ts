import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';

export type Promotion = {
  id: number;
  title: string;
  description: string;
  price?: number;
  badge?: string;
};

@Injectable({ providedIn: 'root' })
export class PromotionsService {
  private baseUrl = 'http://localhost:8080/api/promotions';

  constructor(private http: HttpClient) {}

  // If backend isn't ready, swap the line below to return `mock()` for demo.
  getPromotions(): Observable<Promotion[]> {
    //return this.http.get<Promotion[]>(this.baseUrl);
    return this.mock();
  }

  private mock(): Observable<Promotion[]> {
    return of([
      { id: 1, title: '2 for 1 Chunky Chocolate', description: 'Double the chunky, double the joy.', price: 4.99, badge: 'BOGO' },
      { id: 2, title: 'Salted Caramel 20% Off', description: 'Sweet meets salty in a perfect little storm.', price: 3.20, badge: 'SALE' },
      { id: 3, title: 'Oatmeal Honey Bundle', description: '6-pack for cozy family moments.', price: 9.99 }
    ]);
  }
}
