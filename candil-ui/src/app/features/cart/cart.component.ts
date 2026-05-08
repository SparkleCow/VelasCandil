import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import { ShoppingCartResponseDto, CartItemResponseDto } from '../../shared/models/cart.models';
 
@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDividerModule,
  ],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss'],
})
export class CartComponent implements OnInit {
  private readonly cartService = inject(CartService);
  private readonly orderService = inject(OrderService);
  private readonly router = inject(Router);
 
  cart = signal<ShoppingCartResponseDto | null>(null);
  loading = signal(true);
  checkingOut = signal(false);
  error = signal('');
 
  ngOnInit(): void {
    this.loadCart();
  }
 
  loadCart(): void {
    this.loading.set(true);
    this.cartService.getCart().subscribe({
      next: (data) => {
        this.cart.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar el carrito.');
        this.loading.set(false);
      },
    });
  }
 
  increase(item: CartItemResponseDto): void {
    this.cartService.increaseItem(item.candleId).subscribe({
      next: (data) => this.cart.set(data),
    });
  }
 
  decrease(item: CartItemResponseDto): void {
    this.cartService.decreaseItem(item.candleId).subscribe({
      next: (data) => this.cart.set(data),
    });
  }
 
  remove(item: CartItemResponseDto): void {
    this.cartService.removeItem(item.candleId).subscribe({
      next: (data) => this.cart.set(data),
    });
  }
 
  clearCart(): void {
    this.cartService.clearCart().subscribe({
      next: (data) => this.cart.set(data),
    });
  }
 
  checkout(): void {
    if (this.checkingOut()) return;
    this.checkingOut.set(true);
    this.error.set('');
 
    this.orderService.checkout().subscribe({
      next: (order) => {
        this.checkingOut.set(false);
        if (order.checkoutUrl) {
          // Redirige a Mercado Pago Sandbox
          window.location.href = order.checkoutUrl;
        } else {
          this.error.set('No se obtuvo una URL de pago. Intenta de nuevo.');
        }
      },
      error: () => {
        this.checkingOut.set(false);
        this.error.set('Error al iniciar el pago. Intenta de nuevo.');
      },
    });
  }
 
  get isEmpty(): boolean {
    return !this.cart()?.items?.length;
  }
 
  formatCOP(value: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(value);
  }
}
