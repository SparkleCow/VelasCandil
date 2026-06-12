import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { OrderService } from '../../../../core/services/order.service';
import { OrderResponseDto } from '../../../../shared/models/order.models';
import { NavbarComponent } from '../../../../shared/components/navbar/navbar.component';

@Component({
  selector: 'app-order-success',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './order-success.component.html',
  styleUrl: './order-success.component.scss',
})
export class OrderSuccessComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly orderService = inject(OrderService);

  readonly order = signal<OrderResponseDto | null>(null);
  readonly loading = signal(true);

  ngOnInit(): void {
    const orderId = this.route.snapshot.queryParamMap.get('orderId');
    if (!orderId) {
      this.router.navigate(['/home']);
      return;
    }

    this.orderService.getOrderById(Number(orderId)).subscribe({
      next: (o) => {
        this.order.set(o);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  goToHome(): void { this.router.navigate(['/home']); }
  goToOrders(): void { this.router.navigate(['/orders']); }
}