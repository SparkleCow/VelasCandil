import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { OrderService } from '../../../../core/services/order.service';
import { OrderResponseDto, OrderStatus } from '../../../../shared/models/order.models';
import { MatCardModule } from "@angular/material/card";

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatCardModule
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

  statusIcon(status: OrderStatus): string {
    const icons: Record<OrderStatus, string> = {
      PENDING: 'schedule',
      IN_PROCESS: 'autorenew',
      PAID: 'check_circle',
      FAILED: 'cancel',
      CANCELLED: 'block',
      REFUNDED: 'undo',
    };
    return icons[status] ?? 'help_outline';
  }

  goToHome(): void {
    this.router.navigate(['/home']);
  }
}