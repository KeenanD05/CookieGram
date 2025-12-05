import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-order-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order-management.component.html',
  styleUrls: ['./order-management.component.css']
})
export class OrderManagementComponent implements OnInit {

  orders: any[] = [];
  loading = true;
  errorMessage = '';

  statuses = [
    'PENDING', 'BAKING', 'PACKAGED', 'READY_FOR_PICKUP',
    'SHIPPED', 'DELIVERED', 'CANCELLED'
  ];

  filters: any = {
    status: '',
    deliveryPreset: '',
    deliveryFrom: '',
    deliveryTo: '',
    customerEmail: '',
    totalMin: '',
    totalMax: '',
    orderNumber: ''
  };

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.loading = true;
    this.errorMessage = '';

    this.http.get<any>('http://localhost:8080/api/staff/orders?page=0&size=20',
      {withCredentials:true}
    )
      .subscribe({
        next: res => {
          this.orders = res.content || [];
          this.loading = false;
        },
        error: err => {
          this.errorMessage = "Error loading orders";
          this.loading = false;
        }
      });
  }

  onDeliveryPresetChange() {
    if (this.filters.deliveryPreset === 'today') {
      const today = new Date().toISOString().split("T")[0];
      this.filters.deliveryFrom = today;
      this.filters.deliveryTo = today;
    } else if (this.filters.deliveryPreset === 'range') {
      this.filters.deliveryFrom = '';
      this.filters.deliveryTo = '';
    } else {
      this.filters.deliveryFrom = '';
      this.filters.deliveryTo = '';
    }
  }

  applyFilters() {
  const payload = {
    status: this.filters.status || null,
    requiredShippingDateStart: this.filters.deliveryFrom || null,
    requiredShippingDateEnd: this.filters.deliveryTo || null,
    customerEmail: this.filters.customerEmail || null,
    minAmount: this.filters.totalMin || null,
    maxAmount: this.filters.totalMax || null,
    orderNumber: this.filters.orderNumber || null,
    page: 0,
    size: 20,
    sortBy: "orderDate",
    sortDirection: "desc"
  };

  this.loading = true;
  this.errorMessage = '';

  this.http.post<any>(
    'http://localhost:8080/api/staff/orders/filter',
    payload,
    { withCredentials: true }
  )
  .subscribe({
    next: res => {
      this.orders = res.content ?? [];
      this.loading = false;
    },
    error: err => {
      if (err.status === 404) {
        // 🔥 FIX: backend returns 404 when no results
        this.orders = [];  // <-- CLEAR THE LIST!
        this.loading = false;
        return;
      }

      // Any other error
      this.errorMessage = "Error applying filters.";
      this.loading = false;
    }
  });
}


  viewOrder(orderId: number) {
    this.router.navigate(['/employee/orders', orderId]);
  }

  formatStatus(status: string): string {
    return status
      .toLowerCase()
      .replace(/_/g, ' ')
      .replace(/\b\w/g, c => c.toUpperCase());
  }

  goBack() {
    this.router.navigate(['/employee']);
  }
}
