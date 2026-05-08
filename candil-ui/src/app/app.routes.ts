import { Routes } from '@angular/router';
import { AUTH_ROUTES } from './features/auth/auth.routes';
import { CANDLE_ROUTES } from './features/candles/candles.routes';

import { authGuard } from './core/guards/auth.guard';
import { ORDER_ROUTES } from './features/orders/orders.routes';

export const routes: Routes = [
  ...AUTH_ROUTES,
  ...CANDLE_ROUTES,
  ...ORDER_ROUTES,
  {
    path: 'cart',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/cart/cart.component').then(m => m.CartComponent),
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/profile/pages/profile/profile.component').then(m => m.ProfileComponent),
  },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: '**', redirectTo: 'home' },
];