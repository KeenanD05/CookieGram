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
import { AdminOrderDetailsComponent } from './admin/order-details/admin-order-details.component';
import { AdminOrderManagementComponent } from './admin/order-management/admin-order-management.component';
import { AddEmployeeComponent } from './admin/add-employee/add-employee.component';
import { OrderManagementComponent} from './employee/order-management/order-management.component';
import { OrderDetailsComponent } from './employee/order-details/order-details.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'signup', loadComponent: () => import('./signup/signup.component').then(m => m.SignupComponent) },
  { path: 'cart', component: CartComponent },

  {
    path: '',
    component: ShellComponent,
    children: [
      { path: '', component: LandingComponent },
      { path: 'products', component: ProductsComponent },
      { path: 'checkout', loadComponent: () => import('./checkout/checkout.component').then(m => m.CheckoutComponent) },
      { path: 'thank-you', loadComponent: () => import('./thank-you/thank-you.component').then(m => m.ThankYouComponent) },

      {
        path: 'customer',
        component: CustomerComponent,
        canActivate: [roleGuard],
        data: { roles: ['ROLE_USER'] }
      },

      // EMPLOYEE ROUTES
      {
      path: 'employee',
       
      canActivate: [roleGuard],
      data: { roles: ['ROLE_STAFF', ] },
      children: [
      {path: '', component: EmployeeComponent },
      {path: 'orders', component: OrderManagementComponent},
      {path: 'orders/:id',component: OrderDetailsComponent}

        ]
      },

      // ADMIN ROUTES
      {
        path: 'admin',
        component: AdminComponent,
        canActivate: [roleGuard],
        data: { roles: ['ROLE_ADMIN'] },
        children: [
          { path: 'add-employee', component: AddEmployeeComponent },
          { path: 'orders', component: AdminOrderManagementComponent },
          { path: 'orders/:id', component: AdminOrderDetailsComponent }
        ]
      },

      // CUSTOMER ORDER VIEW
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
