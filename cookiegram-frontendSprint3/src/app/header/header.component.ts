import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, NavigationEnd } from '@angular/router';
import { Subscription, filter } from 'rxjs';
import { CartService } from '../services/cart.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {
  cartCount = 0;

  isCustomerRoute = false;
  isProductsRoute = false;
  isLoggedIn = false;

  private routerSub?: Subscription;

  constructor(
    private cart: CartService,
    private router: Router,
    private auth: AuthService
  ) {}

  ngOnInit() {
    // Cart badge
    this.cart.cartCount$.subscribe(count => (this.cartCount = count));

    // Auth status
    this.isLoggedIn = this.auth.isLoggedIn();

    // Initial route flags
    this.updateRouteFlags(this.router.url);

    // Listen for route changes
    this.routerSub = this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe((e: any) => {
        const url = e.urlAfterRedirects || e.url;
        this.updateRouteFlags(url);
        this.isLoggedIn = this.auth.isLoggedIn(); // refresh login flag on every nav
      });
  }

  ngOnDestroy() {
    this.routerSub?.unsubscribe();
  }

  private updateRouteFlags(url: string) {
    const cleanUrl = url.split('?')[0];

    this.isCustomerRoute = cleanUrl.startsWith('/customer');
    this.isProductsRoute = cleanUrl.startsWith('/products');
  }

  // ---------- NAV ACTIONS ----------

  goHome() {
    this.router.navigate(['/']);
  }

  goLogin() {
    this.router.navigate(['/login']);
  }

  goSignup() {
    this.router.navigate(['/signup']);
  }

  goMyAccount() {
    this.router.navigate(['/customer']);
  }

  onLogout() {
    // Call backend logout and then send user to landing
    this.auth.logout().subscribe({
      next: () => {
        this.isLoggedIn = false;
        this.router.navigate(['/']);
      },
      error: () => {
        // even if logout fails, nuke local role + go home
        localStorage.removeItem('cg_role');
        this.isLoggedIn = false;
        this.router.navigate(['/']);
      }
    });
  }

  goCart() {
    this.router.navigate(['/cart']);
  }
}
