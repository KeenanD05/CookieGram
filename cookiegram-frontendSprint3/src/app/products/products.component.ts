import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService } from '../services/cart.service';
import { Router } from '@angular/router';
import { BagService } from '../services/bag.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-product',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
  <section class="product-page">
    
    <!-- SUCCESS MESSAGE -->
    <div *ngIf="successMessage" class="success-banner">
      {{ successMessage }}
    </div>

    <h1>CookieGram</h1>
    <p class="price">Base Price: $2.99 per cookie</p>

    <form>
      <!-- Icing selection -->
      <label>Icing Flavor</label>
      <select [(ngModel)]="icing" name="icing">
        <option *ngFor="let f of icingOptions" [value]="f">{{ f }}</option>
      </select>

      <!-- Optional message -->
      <label>Optional Message</label>
      <input type="text" [(ngModel)]="message" name="message" placeholder="Happy Birthday!" />

      <!-- Quantity -->
      <label>Quantity</label>
      <input type="number" [(ngModel)]="quantity" name="quantity" min="1" />

      <!-- Live Total -->
      <p class="total">Total: {{ total | currency:'USD' }}</p>

      <!-- Action Buttons -->
      <div class="actions">
        <button type="button" (click)="addToBag()">Add to Cart</button>
        <button type="button" (click)="goToCart()">Go to Checkout</button>
      </div>
    </form>

    <button class="back" (click)="goBack()">← Back</button>
  </section>
  `,
  styles: [`
    .product-page { 
      display:flex; 
      flex-direction:column; 
      gap:1rem; 
      max-width:420px; 
      margin:auto; 
      padding:2rem; 
    }

    form { display:flex; flex-direction:column; gap:.8rem; }

    .actions { display:flex; justify-content:space-between; margin-top:1rem; }

    input, select, button { padding:.5rem; }

    .price, .total { font-weight:bold; }

    .back { margin-top:2rem; }

    /* SUCCESS BANNER */
    .success-banner {
      background: #d1f7d6;
      color: #1a6b2b;
      padding: .9rem 1rem;
      border-radius: 8px;
      margin-bottom: 1rem;
      border: 1px solid #a0e6af;
      font-weight: 500;
      text-align:center;
      animation: fadeIn 0.25s ease;
    }

    @keyframes fadeIn {
      from { opacity: 0; transform: translateY(-6px); }
      to { opacity: 1; transform: translateY(0); }
    }
  `]
})
export class ProductsComponent {
  icingOptions = ['Vanilla', 'Chocolate', 'Strawberry', 'Mint'];
  icing = 'Vanilla';
  message = '';
  quantity = 1;
  basePrice = 2.99;

  successMessage: string = '';  // <-- success banner text

  constructor(
    private cart: CartService,
    private auth: AuthService,
    private bag: BagService,
    private router: Router
  ) {}

  get total() {
    return this.quantity * this.basePrice;
  }

  addToBag() {
    this.cart.addToCart({
      productId: 1,
      icing: this.icing,
      message: this.message,
      quantity: this.quantity,
      price: this.total
    });

    this.showSuccess("Added to cart!");
  }

  showSuccess(message: string) {
    this.successMessage = message;

    // Auto-hide after 3 seconds
    setTimeout(() => {
      this.successMessage = '';
    }, 3000);
  }

  goToCart() {
    this.router.navigate(['/cart']);
  }

  goBack() {
    if (this.auth.isLoggedIn()) {
      this.router.navigate(['/customer']);
    } else {
      this.router.navigate(['/']);
    }
  }
}
