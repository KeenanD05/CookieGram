import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink],
  template: `
  <header class="cg-header">
    <div class="brand">
      <span class="logo">🍪</span>
      <a routerLink="/" class="title">CookieGram</a>
    </div>
    <nav class="nav">
      <a routerLink="/" class="link">Home</a>
      <a routerLink="/customer" class="link">Promos</a>
      <a routerLink="/employee" class="link">Employee</a>
      <a routerLink="/admin" class="link">Admin</a>
      <a routerLink="/login" class="cta">Login</a>
    </nav>
  </header>
  `,
  styles: [`
    .cg-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(to right, #fff6ef, #ffe2ca);
  padding: 1.2rem 3rem;
  box-shadow: 0 4px 12px rgba(139, 86, 36, 0.15);
}

.brand {
  font-size: 1.6rem;
  font-weight: 800;
  display: flex;
  align-items: center;
  gap: 0.4rem;
}

.brand .title {
  color: var(--primary-dark);
  text-transform: uppercase;
  font-weight: 800;
}

nav a {
  margin-left: 1.4rem;
  font-weight: 600;
  font-size: 1rem;
}

.login-btn {
  background: var(--primary);
  color: white;
  padding: 0.5rem 1.2rem;
  border-radius: 8px;
}

.login-btn:hover {
  background: var(--primary-dark);
}

  `]
})
export class HeaderComponent {}
