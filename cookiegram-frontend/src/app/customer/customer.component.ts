import { Component, OnInit } from '@angular/core';
import { PromotionsService, Promotion } from '../services/promotions.service';
import { CommonModule, NgFor } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-customer',
  standalone: true,
  imports: [NgFor, CommonModule],
  template: `
  <section class="page">

    <!-- Header Action Bar -->
    <div class="actions">
      <button class="btn" (click)="orderNow()">Place an Order</button>
      <button class="btn" (click)="viewBag()">My Bag</button>
      <button class="btn" (click)="logout()">Logout</button>

      <!-- <button routerLink="/orders" class="cta">View My Orders</button> -->

    </div>

    <h1 class="title">Today’s Promotions</h1>
    <p class="subtitle">Handpicked delights—fresh from the oven.</p>

    <div class="grid">
      <article class="promo" *ngFor="let p of promos">
        <div class="tag">{{ p.badge || 'Special' }}</div>
        <h3>{{ p.title }}</h3>
        <p>{{ p.description }}</p>

        <div class="meta">
          <span class="price" *ngIf="p.price">Now {{ p.price | currency }}</span>
          <span class="cta" (click)="orderNow()">Order</span>
        </div>
      </article>
    </div>
  </section>
  `,
  styles: [`
    .page { display:grid; gap:.8rem; padding:1.5rem; }
    .actions { display:flex; justify-content:center; gap:1rem; margin-bottom:1.5rem; }
    .btn {
      background:#d4a373; color:#fff; border:none; padding:.7rem 1.4rem;
      border-radius:8px; cursor:pointer; font-weight:600;
      box-shadow:0 4px 10px rgba(0,0,0,.15);
      transition:all .25s;
    }
    .btn:hover { background:#b5835a; transform:translateY(-2px); }

    .title { margin:.2rem 0; color:#5a3516; text-align:center; }
    .subtitle { margin:0 auto 1rem; text-align:center; color:#7a4f2e; opacity:.9; }

    .grid { display:grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1rem; }
    .promo {
      border:1px solid #f0d9c5; border-radius: 1rem; background:#fffdfb; padding:1rem;
      box-shadow: 0 8px 18px rgba(139,86,36,.07);
    }
    .tag {
      display:inline-block; background:#ffe8d7; border:1px solid #f0d9c5; color:#6b4226;
      padding:.15rem .5rem; border-radius:.5rem; font-size:.8rem; margin-bottom:.4rem;
    }
    .meta { display:flex; align-items:center; justify-content:space-between; margin-top:.6rem; }
    .price { font-weight:700; color:#5a3516; }
    .cta { font-weight:600; color:#6b4226; cursor:pointer; }
    .cta:hover { text-decoration: underline; }
  `]
})
export class CustomerComponent implements OnInit {
  promos: Promotion[] = [];

  constructor(
    private promosSvc: PromotionsService,
    private router: Router,
    private auth: AuthService
  ) {}

  ngOnInit() {
    this.promosSvc.getPromotions().subscribe(p => this.promos = p);
  }

  orderNow() {
    this.router.navigate(['/products']);
  }

  viewBag() {
    this.router.navigate(['/cart']);
  }

  viewOrders() {
    this.router.navigate(['/orders']);
  }

  logout() {
  this.auth.logout();
  this.router.navigate(['/']);
}
}
