import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';


export interface CartItem {
  productId: number;
  icing: string;
  message?: string;
  quantity: number;
  price: number;
}



@Injectable({ providedIn: 'root' })
export class CartService {
  public items: CartItem[] = [];
  public cartCount = new BehaviorSubject<number>(0);
  cartCount$ = this.cartCount.asObservable();

  addToCart(item: CartItem) {
    this.items.push(item);
    this.cartCount.next(this.items.length);
  }

  getItems(): CartItem[] {
    return this.items;
  }

  removeItem(index: number) {
    this.items.splice(index, 1);
    this.cartCount.next(this.items.length);
  }

  clearCart() {
    this.items = [];
    this.cartCount.next(0);
  }

  
}
