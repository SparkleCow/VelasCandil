import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-order-pending',
  standalone: true,
  imports: [CommonModule, RouterModule, MatButtonModule, MatIconModule],
  templateUrl: './order-pending.component.html',
  styleUrl: './order-pending.component.scss',
})
export class OrderPendingComponent {
  private readonly router = inject(Router);

  goToOrders(): void { this.router.navigate(['/orders']); }
  goToHome(): void { this.router.navigate(['/home']); }
}