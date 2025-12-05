import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';

import { AuthService } from '../services/auth.service';
@Component({
  selector: 'app-employee',
  standalone: true,
  imports: [RouterModule, CommonModule],
  template: `

 <header class="admin-header">
  <div class="admin-left">
    <span class="admin-logo-icon">🍪</span>
    <span class="admin-logo-text" routerLink="/admin">COOKIEGRAM</span>
   
  </div>

  <div class="admin-right">
    <button class="logout-btn" (click)="logout()">Logout</button>
  </div>
</header>
  <section class="page">
    <h1 class="title">Employee Dashboard</h1>
    <p class="subtitle">Monitor cookie orders, oven activity, and delivery runs — all in one place.</p>

    <!-- Order Management Button -->
    <button class="manage-btn" routerLink="/employee/orders">
      Manage Orders
    </button>

    <!-- Quick Stats -->
    <div class="summary">
      <div class="card">
        <h2>12</h2>
        <p>Open Orders</p>
      </div>
      <div class="card">
        <h2>4</h2>
        <p>In Ovens</p>
      </div>
      <div class="card">
        <h2>6</h2>
        <p>Out for Delivery</p>
      </div>
    </div>

    <!-- Detailed Panels -->
    <!-- <div class="grid">
      <div class="panel">
        <h3>🧾 Order Queue</h3>
        <ul>
          <li><b>CG-1031</b> — 12× Choco Cookies · <span class="status ready">Ready</span></li>
          <li><b>CG-1032</b> — 8× Choco Cookies · <span class="status mixing">Mixing</span></li>
          <li><b>CG-1033</b> — 10× Vanilla Icing Cookies· <span class="status baking">Baking</span></li>
        </ul>
      </div>

      <div class="panel">
        <h3>🔥 Oven Activity</h3>
        <ul>
          <li>Rack A — Choco Cookies (6 min left)</li>
          <li>Rack B — Caramel Swirl (9 min left)</li>
          <li>Rack C — Oatmeal Honey (12 min left)</li>
        </ul>
      </div>

      <div class="panel">
        <h3>🚚 Delivery Runs</h3>
        <ul>
          <li>15:00 → Downtown Core — Order CG-1028</li>
          <li>16:30 → Lakeside — Order CG-1026</li>
          <li>17:15 → Oakridge — Order CG-1024</li>
        </ul>
      </div>
    </div>
  </section> -->
  `,
  styles: [`
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
  .admin-logo-icon {
    font-size: 1.6rem;
  }

  .admin-logo-text {
    font-size: 1.3rem;
    font-weight: 700;
    color: #5a3516;
    cursor: pointer;
  }
  .admin-left {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .admin-right {
    display: flex;
    align-items: center;
    background:none;
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

    .page { display:grid; gap:1rem; padding:1.5rem; }
    .title { color:#5a3516; margin:.2rem 0; font-size:2rem; }
    .subtitle { color:#7a4f2e; opacity:.9; margin:0 0 1.5rem; font-size:1.1rem; }
     
    .manage-btn {
      background:#5a3516;
      color:#fff;
      padding:.7rem 1.4rem;
      border-radius:8px;
      border:none;
      cursor:pointer;
      margin-bottom:1rem;
      font-size:1rem;
      transition:opacity .2s;
    }
    .manage-btn:hover { opacity:0.85; }

    /* Summary cards */
    .summary { display:flex; flex-wrap:wrap; gap:1rem; justify-content:center; }
    .card { background:#fff5ec; border-radius:12px; padding:1rem 2rem; text-align:center; 
            box-shadow:0 3px 8px rgba(0,0,0,0.1); flex:1 1 150px; }
    .card h2 { color:#6b3c1f; margin:0; font-size:1.8rem; }
    .card p { margin:0; color:#8b5a32; }

    /* Grid panels */
    .grid { display:grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap:1.2rem; }
    .panel { background:#fff; border:1px solid #f0d9c5; border-radius:1rem; padding:1.2rem; box-shadow:0 4px 10px rgba(0,0,0,0.05); }
    h3 { margin:.3rem 0 .6rem; color:#6b4226; }
    ul { margin:0; padding-left:1.2rem; line-height:1.6; color:#3c2615; }

    .status { padding:0.1rem 0.5rem; border-radius:8px; font-size:0.8rem; color:#fff; }
    .status.ready { background:#77c38d; }
    .status.baking { background:#e0a458; }
    .status.mixing { background:#c3785f; }
  `]
})
export class EmployeeComponent {
  constructor(
    private router: Router,
    private auth: AuthService
  ) {}

      logout() {
  this.auth.logout();
  this.router.navigate(['/']);
}
}
