import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { ShellComponent } from './layout/shell/shell.component';
import { LandingComponent } from './landing/landing.component';
import { CustomerComponent } from './customer/customer.component';
import { EmployeeComponent } from './employee/employee.component';
import { AdminComponent } from './admin/admin.component';
import { roleGuard } from './guards/role.guard';

export const routes: Routes = [
  // Login stands alone (NO header/footer)
  { path: 'login', component: LoginComponent },

  // Everything else goes through the Shell (WITH header/footer)
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: '', component: LandingComponent }, // default page
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
}

    ]
  },

  { path: '**', redirectTo: 'login' }
];
