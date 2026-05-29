import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { UserService } from '../services/user.service';

export const adminGuard: CanActivateFn = () => {
  const userService = inject(UserService);
  const router = inject(Router);

  if (!userService.isLogged()) {
    router.navigate(['/login']);
    return false;
  }

  if (!userService.isAdmin()) {
    router.navigate(['/candles']);
    return false;
  }

  return true;
};
