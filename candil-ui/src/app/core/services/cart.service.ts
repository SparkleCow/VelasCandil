import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ShoppingCartResponseDto } from '../../shared/models/cart.models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/v1/cart`;

  getCart(): Observable<ShoppingCartResponseDto> {
    return this.http.get<ShoppingCartResponseDto>(this.base);
  }

  addItem(candleId: number): Observable<ShoppingCartResponseDto> {
    return this.http.post<ShoppingCartResponseDto>(
      `${this.base}/items/${candleId}`,
      {},
    );
  }

  increaseItem(candleId: number): Observable<ShoppingCartResponseDto> {
    return this.http.post<ShoppingCartResponseDto>(
      `${this.base}/items/${candleId}/increase`,
      {},
    );
  }

  decreaseItem(candleId: number): Observable<ShoppingCartResponseDto> {
    return this.http.delete<ShoppingCartResponseDto>(
      `${this.base}/items/${candleId}/decrease`,
    );
  }

  removeItem(candleId: number): Observable<ShoppingCartResponseDto> {
    return this.http.delete<ShoppingCartResponseDto>(
      `${this.base}/items/${candleId}`,
    );
  }

  clearCart(): Observable<ShoppingCartResponseDto> {
    return this.http.delete<ShoppingCartResponseDto>(`${this.base}/clear`);
  }

  // Backward compatibility for existing callers.
  getOrCreateCart(): Observable<ShoppingCartResponseDto> {
    return this.getCart();
  }
}
