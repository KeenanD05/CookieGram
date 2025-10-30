import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

export type Promotion = {
 id: number;
  title: string;
  description: string;
  price: number;
  badge?: string;
  imageUrl?: string;
};

@Injectable({ providedIn: 'root' })
export class PromotionsService {
  private baseUrl = 'http://localhost:8080/api/public/cookies';

  constructor(private http: HttpClient) {}

  
  getPromotions(): Observable<Promotion[]> {
    return this.http.get<any[]>(this.baseUrl).pipe(
    map(cookies =>
      cookies.map(cookie => ({
        id: cookie.id,
        title: cookie.name,
        description: cookie.description,
        price: cookie.basePrice,
        badge: cookie.discount > 0 ? 'SALE' : cookie.customizable ? 'BOGO' : undefined,
        imageUrl: cookie.imageUrl
      }))));
    
  }

  
}
