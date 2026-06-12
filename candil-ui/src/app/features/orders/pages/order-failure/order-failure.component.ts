import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';


@Component({
  selector: 'app-order-failure',
  standalone: true,
  imports: [CommonModule, RouterModule, MatButtonModule, MatIconModule],
  templateUrl: './order-failure.component.html',
  styleUrl: './order-failure.component.scss',
})
export class OrderFailureComponent {
  private readonly router = inject(Router);

  goToCart(): void { this.router.navigate(['/cart']); }
  goToHome(): void { this.router.navigate(['/home']); }
}