import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-order-details',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-order-details.component.html',
  styleUrls: ['./admin-order-details.component.css']
})
export class AdminOrderDetailsComponent implements OnInit {

  order: any = null;
  loading = true;
  errorMessage = '';

  newStatus = '';
  updateError = '';
  successMessage = '';

  statuses = [
    'PENDING', 'BAKING', 'PACKAGED', 'READY FOR PICKUP',
    'SHIPPED', 'DELIVERED', 'CANCELLED',
  ];

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadOrder(id);
  }

  loadOrder(id: number) {
    this.http.get<any>(
      `http://localhost:8080/api/staff/orders/${id}`,
      { withCredentials: true }
    )
    .subscribe({
      next: res => {
        this.order = res;
        this.loading = false;
      },
      error: err => {
        this.errorMessage = "Unable to load order.";
        this.loading = false;
      }
    });
  }

  updateStatus() {
    if (!this.newStatus) {
      this.updateError = "Please select a status.";
      this.successMessage = '';
      return;
    }

    this.updateError = '';
    this.successMessage = '';

    this.http.post<any>(
      `http://localhost:8080/api/staff/orders/${this.order.id}?orderStatus=${this.newStatus}`,
      {},
      { withCredentials: true }
    )
    .subscribe({
      next: () => {
        this.order.status = this.newStatus;
        this.successMessage = "Order status updated successfully!";
      },
      error: () => {
        this.updateError = "Failed to update status.";
      }
    });
  }

  goBack() {
    this.router.navigate(['/admin/orders']);
  }
}
