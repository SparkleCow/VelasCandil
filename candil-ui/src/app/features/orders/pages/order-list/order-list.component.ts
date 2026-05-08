import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { OrderService } from '../../../../core/services/order.service';
import { OrderResponseDto, OrderStatus } from '../../../../shared/models/order.models';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule,
  ],
  templateUrl: './order-list.component.html',
  styleUrl: './order-list.component.scss',
})
export class OrderListComponent implements OnInit {
  private readonly orderService = inject(OrderService);
  private readonly router = inject(Router);

  readonly orders = signal<OrderResponseDto[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  ngOnInit(): void {
    this.orderService.getMyOrders().subscribe({
      next: (data) => {
        this.orders.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar tus pedidos.');
        this.loading.set(false);
      },
    });
  }

  statusLabel(status: OrderStatus): string {
    const labels: Record<OrderStatus, string> = {
      PENDING: 'Pendiente',
      IN_PROCESS: 'En proceso',
      PAID: 'Pagado',
      FAILED: 'Fallido',
      CANCELLED: 'Cancelado',
      REFUNDED: 'Reembolsado',
    };
    return labels[status] ?? status;
  }

  statusColor(status: OrderStatus): string {
    const colors: Record<OrderStatus, string> = {
      PENDING: 'default',
      IN_PROCESS: 'accent',
      PAID: 'primary',
      FAILED: 'warn',
      CANCELLED: 'warn',
      REFUNDED: 'accent',
    };
    return colors[status] ?? 'default';
  }

  goToHome(): void {
    this.router.navigate(['/home']);
  }
}
