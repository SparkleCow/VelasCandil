import { Routes } from '@angular/router';
import { authGuard } from '../../core/guards/auth.guard';
import { adminGuard } from '../../core/guards/admin.guard';

export const CANDLE_ROUTES: Routes = [
  {
    path: 'home',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'candles',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/candle-list/candle-list.component').then(m => m.CandleListComponent)
  },
  {
    path: 'candles/create',
    canActivate: [authGuard /*adminGuard*/],
    loadComponent: () =>
      import('./pages/candle-create/candle-create.component').then(m => m.CandleCreateComponent)
  },
  {
    path: 'candles/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/candle-detail/candle-detail.component').then(m => m.CandleDetailComponent)
  }
];