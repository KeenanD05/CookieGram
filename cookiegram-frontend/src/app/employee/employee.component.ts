import { Component } from '@angular/core';

@Component({
  selector: 'app-employee',
  standalone: true,
  template: `
  <section class="page">
    <h1 class="title">Employee Dashboard</h1>
    <p class="subtitle">Monitor cookie orders, oven activity, and delivery runs — all in one place.</p>

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
    <div class="grid">
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
  </section>
  `,
  styles: [`
    .page { display:grid; gap:1rem; padding:1.5rem; }
    .title { color:#5a3516; margin:.2rem 0; font-size:2rem; }
    .subtitle { color:#7a4f2e; opacity:.9; margin:0 0 1.5rem; font-size:1.1rem; }

    /* Summary cards */
    .summary {
      display:flex;
      flex-wrap:wrap;
      gap:1rem;
      justify-content:center;
    }
    .card {
      background:#fff5ec;
      border-radius:12px;
      padding:1rem 2rem;
      text-align:center;
      box-shadow:0 3px 8px rgba(0,0,0,0.1);
      flex:1 1 150px;
    }
    .card h2 { color:#6b3c1f; margin:0; font-size:1.8rem; }
    .card p { margin:0; color:#8b5a32; }

    /* Detailed grid */
    .grid {
      display:grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap:1.2rem;
    }
    .panel {
      background:#fff;
      border:1px solid #f0d9c5;
      border-radius:1rem;
      padding:1.2rem;
      box-shadow:0 4px 10px rgba(0,0,0,0.05);
    }
    h3 { margin:.3rem 0 .6rem; color:#6b4226; }
    ul { margin:0; padding-left:1.2rem; line-height:1.6; color:#3c2615; }

    /* Status badges */
    .status {
      padding:0.1rem 0.5rem;
      border-radius:8px;
      font-size:0.8rem;
      color:#fff;
    }
    .status.ready { background:#77c38d; }
    .status.baking { background:#e0a458; }
    .status.mixing { background:#c3785f; }
  `]
})
export class EmployeeComponent {}
