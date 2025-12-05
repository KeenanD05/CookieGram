import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [RouterModule, CommonModule],
  template: `
    <!-- ADMIN HEADER -->
<header class="admin-header">
  <div class="admin-left">
    <span class="admin-logo-icon">🍪</span>
    <span class="admin-logo-text" routerLink="/admin">COOKIEGRAM</span>
   
  </div>

  <div class="admin-right">
    <button class="logout-btn" (click)="logout()">Logout</button>
  </div>
</header>

<!-- ADMIN PAGE CONTENT -->
<section class="page">

  <!-- SHOW DASHBOARD ONLY ON /admin -->
  <ng-container *ngIf="onDashboard">

    <h1 class="title">Admin Dashboard</h1>
     
    <p class="subtitle">
      High-level controls for people, products, and performance.
    </p>

    <nav class="admin-nav">
      <button routerLink="/admin/add-employee">Create Employee</button>
      
      <button class="manage-btn" routerLink="/admin/orders">
      Manage Orders
    </button>
    </nav>

    <div class="grid">
      <div class="panel">
        <h3>Users</h3>
        <p>Manage roles & access. (Coming soon)</p>
      </div>
      <div class="panel">
        <h3>Catalog</h3>
        <p>Create promos, update pricing. (Coming soon)</p>
      </div>
      <div class="panel">
        <h3>Reports</h3>
        <p>Sales, retention, conversion. (Coming soon)</p>
      </div>
    </div>

  </ng-container>

  <!-- CHILD ADMIN ROUTES -->
  <router-outlet></router-outlet>

</section>
  `,
  styles: [`
  /* ===== ADMIN HEADER (MINIMAL + CLEAN) ===== */
  .admin-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fffdf9;
    padding: 0.55rem 1.25rem;
    border-bottom: 1px solid #e6d7c8;
    position: sticky;
    top: 0;
    z-index: 50;
  }

  .admin-left {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .admin-logo-icon {
    font-size: 1.6rem;
  }

  .admin-logo-text {
    font-size: 1.3rem;
    font-weight: 700;
    color: #5a3516;
    cursor: pointer;
  }

  .admin-title {
    font-size: 1.1rem;
    font-weight: 600;
    margin-left: 0.75rem;
    color: #7a4f2e;
  }

  .admin-right {
    display: flex;
    align-items: center;
  }

  .logout-btn {
    background: #5a3516;
    color: #fff;
    border: none;
    padding: 0.4rem 0.9rem;
    border-radius: 0.4rem;
    font-size: 0.9rem;
    cursor: pointer;
    transition: 0.15s ease;
  }

  .logout-btn:hover {
    opacity: 0.9;
  }

  /* ===== PAGE LAYOUT FIXES ===== */
  .page {
    padding: 1.5rem 2rem;          /* NEW horizontal padding */
    display: flex;
    flex-direction: column;
    gap: .75rem;
    min-height: calc(100vh - 140px); /* ensures footer sticks to the bottom */
    box-sizing: border-box;
  }

  .title {
    color:#5a3516;
    margin: 0.25rem 0;
  }

  .subtitle {
    color:#7a4f2e;
    opacity:.9;
    margin:0 0 1rem;
  }

  /* ===== ADMIN NAV ===== */
  .admin-nav {
    display:flex;
    gap:.5rem;
    margin-bottom:1rem;
  }

  .admin-nav button {
    padding:.5rem 1rem;
    border:none;
    border-radius:.5rem;
    background:#e0c3a6;
    color:#5a3516;
    cursor:pointer;
    transition:.2s;
  }

  .admin-nav button:hover {
    opacity:.85;
  }

  /* ===== DASHBOARD GRID ===== */
  .grid {
    display:grid;
    grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
    gap: 1rem;
  }

  .panel {
    background:#fff;
    border:1px solid #f0d9c5;
    border-radius:1rem;
    padding:1rem;
  }
`]

})
export class AdminComponent {

  constructor(
    private router: Router,
    private auth: AuthService
  ) {}

  get onDashboard(): boolean {
    return this.router.url === '/admin';
  }

    logout() {
  this.auth.logout();
  this.router.navigate(['/']);
}
}
