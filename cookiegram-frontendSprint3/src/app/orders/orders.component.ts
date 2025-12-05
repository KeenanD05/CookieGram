import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { OrdersService } from '../services/orders.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-orders',
  imports: [CommonModule],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css'
})
export class OrdersComponent implements OnInit {
  orders: any[] = [];

  constructor(private orderService: OrdersService) {}

  ngOnInit() {
    this.orderService.getMyOrders().subscribe({
      next: (data) => (this.orders = data),
      error: (err) => console.error('Orders fetch failed:', err)
    });
  }
}

