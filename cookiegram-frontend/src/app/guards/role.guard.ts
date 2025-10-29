import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);
  const auth = inject(AuthService);

  const allowed: string[] = route.data['roles'] ?? [];
  const role = auth.getRole();

  if (role && allowed.includes(role)) return true;

  // Not allowed → kick back to landing
  router.navigateByUrl('/');
  return false;
};
