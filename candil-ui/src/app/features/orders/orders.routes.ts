import { Routes } from '@angular/router';
import { authGuard } from '../../core/guards/auth.guard';

export const ORDER_ROUTES: Routes = [
  {
    path: 'orders',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/order-list/order-list.component').then(m => m.OrderListComponent),
  },
  {
    path: 'orders/success',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/order-success/order-success.component').then(m => m.OrderSuccessComponent),
  },
  {
    path: 'orders/failure',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/order-failure/order-failure.component').then(m => m.OrderFailureComponent),
  },
  {
    path: 'orders/pending',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/order-pending/order-pending.component').then(m => m.OrderPendingComponent),
  },
];
