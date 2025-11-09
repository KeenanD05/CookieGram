import { Component } from '@angular/core';

@Component({
  selector: 'app-admin',
  standalone: true,
  template: `
  <section class="page">
    <h1 class="title">Admin Dashboard</h1>
    <p class="subtitle">High-level controls for people, products, and performance.</p>

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
  </section>
  `,
  styles: [`
    .page { display:grid; gap:.6rem; }
    .title { color:#5a3516; margin:.2rem 0; }
    .subtitle { color:#7a4f2e; opacity:.9; margin:0 0 1rem; }
    .grid { display:grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 1rem; }
    .panel { background:#fff; border:1px solid #f0d9c5; border-radius:1rem; padding:1rem; }
  `]
})
export class AdminComponent {}
