import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { ShellComponent } from './layout/shell/shell.component';
import { LandingComponent } from './landing/landing.component';
import { CustomerComponent } from './customer/customer.component';
import { EmployeeComponent } from './employee/employee.component';
import { AdminComponent } from './admin/admin.component';
import { roleGuard } from './guards/role.guard';
import { ProductsComponent } from './products/products.component';
import { CartComponent } from './cart/cart.component';
import { OrdersComponent } from './orders/orders.component';
// import { ThankYouComponent } from './thank-you/thank-you.component';
// import { CheckoutComponent } from './checkout/checkout.component';
export const routes: Routes = [

  { path: 'login', component: LoginComponent },

  // { path: 'thank-you', component: ThankYouComponent },
  // { path: 'checkout', component: CheckoutComponent },
  { path: 'signup', loadComponent: () => import('./signup/signup.component').then(m => m.SignupComponent) },

  {
    path: 'cart',
    component: CartComponent // we create next
  },

  {
    path: '',
    component: ShellComponent,
    children: [
      { path: '', component: LandingComponent },
      // default page
      { path: 'products', component: ProductsComponent },
      { path: 'checkout', loadComponent: () => import('./checkout/checkout.component').then(m => m.CheckoutComponent) },

      { path: 'thank-you', loadComponent: () => import('./thank-you/thank-you.component').then(m => m.ThankYouComponent) },


      {
        path: 'customer',
        component: CustomerComponent,
        canActivate: [roleGuard],
        data: { roles: ['ROLE_USER'] }
      },
      {
        path: 'employee',
        component: EmployeeComponent,
        canActivate: [roleGuard],
        data: { roles: ['ROLE_STAFF', 'ROLE_EMPLOYEE'] }, // ✅ add this
      },
      {
        path: 'admin',
        component: AdminComponent,
        canActivate: [roleGuard],
        data: { roles: ['ROLE_ADMIN'] }
      },
      {
  path: 'orders',
  component: OrdersComponent,
  canActivate: [roleGuard],
  data: { roles: ['ROLE_USER'] }
}


    ]
  },

  { path: '**', redirectTo: 'login' }
];
