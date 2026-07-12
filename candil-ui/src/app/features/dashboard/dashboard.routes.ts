import { Routes } from '@angular/router';
import { adminGuard } from '../../core/guards/admin.guard';

export const DASHBOARD_ROUTES: Routes = [
  {
    path: 'dashboard',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./dashboard.component').then((m) => m.DashboardComponent),
  },
];
