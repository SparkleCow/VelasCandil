import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrderResponseDto } from '../../shared/models/order.models';
import { environment } from '../../../environments/environment';
 
@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/v1/orders`;
 

  checkout(): Observable<OrderResponseDto> {
    return this.http.post<OrderResponseDto>(`${this.base}/checkout`, {});
  }


  getMyOrders(): Observable<OrderResponseDto[]> {
    return this.http.get<OrderResponseDto[]>(this.base);
  }
 

  getOrderById(orderId: number): Observable<OrderResponseDto> {
    return this.http.get<OrderResponseDto>(`${this.base}/${orderId}`);
  }
}
