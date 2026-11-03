import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Permiso } from './models';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.logueado() ? true : inject(Router).createUrlTree(['/login']);
};

export const permisoGuard: CanActivateFn = route => {
  const auth = inject(AuthService);
  const perms = (route.data['permisos'] ?? []) as Permiso[];
  if (perms.length === 0 || perms.some(p => auth.puede(p))) return true;
  return inject(Router).createUrlTree([auth.inicio()]);
};

export const inicioGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return inject(Router).createUrlTree([auth.inicio()]);
};
