import { Component, OnInit } from '@angular/core';
import { CartService } from '../services/cart.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  
  
  items: any[] = [];

  subtotal = 0;
  shipping = 0.99;
  taxRate = 0.13; // 13% HST example
  total = 0;


  constructor(
    private cart: CartService,
    private router: Router,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    this.items = this.cart.getItems();
    this.subtotal = this.items.reduce((sum, i) => sum + i.price * i.quantity, 0);
    this.total = this.subtotal + this.shipping + (this.subtotal * this.taxRate);
  }

  remove(i: number): void {
    this.cart.removeItem(i);
    this.refresh();
  }

  goBack() {
    if (this.auth.isLoggedIn()) {
      this.router.navigate(['/products']);
    } else {
      this.router.navigate(['/']);
    }
  }

  checkout() {
    this.router.navigate(['/checkout']);
  }
}
