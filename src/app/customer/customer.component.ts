import { Component, OnInit } from '@angular/core';
import { PromotionsService, Promotion } from '../services/promotions.service';
import { NgFor } from '@angular/common';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-customer',
  standalone: true,
  imports: [NgFor, CommonModule],
  template: `
  <section class="page">
    <h1 class="title">Today’s Promotions</h1>
    <p class="subtitle">Handpicked delights—fetching from the oven (server) just for you.</p>

    <div class="grid">
      <article class="promo" *ngFor="let p of promos">
        <div class="tag">{{ p.badge || 'Special' }}</div>
        <h3>{{ p.title }}</h3>
        <p>{{ p.description }}</p>
        <div class="meta">
          <span class="price" *ngIf="p.price">Now {{ p.price | currency }}</span>
          <span class="cta">Add to cravings</span>
        </div>
      </article>
    </div>
  </section>
  `,
  styles: [`
    .page { display:grid; gap:.6rem; }
    .title { margin:.2rem 0; color:#5a3516; }
    .subtitle { margin:0 0 1rem; color:#7a4f2e; opacity:.9; }
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
  constructor(private promosSvc: PromotionsService) {}
  ngOnInit() { this.promosSvc.getPromotions().subscribe(p => this.promos = p); }
}
