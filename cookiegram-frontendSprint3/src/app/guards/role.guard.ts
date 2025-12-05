import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);
  const auth = inject(AuthService);

  const allowed: string[] = route.data['roles'] ?? [];
  const userRoles: string[] = auth.getRole() || [];

  console.log('RoleGuard check → allowed:', allowed, 'stored:', userRoles);

  // ⭐ FIX: Check if ANY user role matches ANY allowed role (case-insensitive)
  const allowedUpper = allowed.map(r => r.toUpperCase());
  const userUpper = userRoles.map(r => r.toUpperCase());

  const hasAccess = userUpper.some(r => allowedUpper.includes(r));

  if (hasAccess) {
    return true;
  }

  router.navigateByUrl('/');
  return false;
};
