import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PromotionsService, Promotion } from '../services/promotions.service';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit {
  promos: Promotion[] = [];

  constructor(private promoService: PromotionsService) {}

  ngOnInit(): void {
    // Automatically fetch promotions on landing page load
    this.promoService.getPromotions().subscribe({
      next: (data) => (this.promos = data),
      error: (err) => console.error('Error fetching promotions:', err)
    });
  }

  goLogin() {
    window.location.href = '/login';
  }
}
