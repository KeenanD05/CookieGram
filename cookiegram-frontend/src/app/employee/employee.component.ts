import { Component } from '@angular/core';

@Component({
  selector: 'app-employee',
  standalone: true,
  template: `
  <section class="page">
    <h1 class="title">Employee Dashboard</h1>
    <p class="subtitle">A quick pulse on orders, bakes, and deliveries.</p>

    <div class="grid">
      <div class="panel">
        <h3>Open Orders</h3>
        <ul>
          <li>CG-1024 · 12× Chunky Chocolate · Ready 2:30 PM</li>
          <li>CG-1025 · 6× Salted Caramel · Mixing</li>
        </ul>
      </div>
      <div class="panel">
        <h3>Oven Queue</h3>
        <ul>
          <li>Rack A — Caramel (8m)</li>
          <li>Rack B — Oatmeal Honey (12m)</li>
        </ul>
      </div>
      <div class="panel">
        <h3>Delivery</h3>
        <ul>
          <li>15:00 · DT Core · Order CG-1023</li>
          <li>16:15 · Lakeside · Order CG-1019</li>
        </ul>
      </div>
    </div>
  </section>
  `,
  styles: [`
    .page { display:grid; gap:.6rem; }
    .title { color:#5a3516; margin:.2rem 0; }
    .subtitle { color:#7a4f2e; opacity:.9; margin:0 0 1rem; }
    .grid { display:grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 1rem; }
    .panel { background:#fff; border:1px solid #f0d9c5; border-radius:1rem; padding:1rem; }
    h3 { margin:.2rem 0 .5rem; color:#6b4226; }
    ul { margin:0; padding-left: 1.1rem; }
  `]
})
export class EmployeeComponent {}
