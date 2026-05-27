import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  IngredientRequest,
  IngredientUpdateRequest,
  IngredientResponse,
  IngredientType,
  Page,
} from '../../shared/models/ingredient.models';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class IngredientService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/v1/ingredients`;

  findAll(page = 0, size = 20): Observable<Page<IngredientResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<IngredientResponse>>(this.base, { params });
  }

  findById(id: number): Observable<IngredientResponse> {
    return this.http.get<IngredientResponse>(`${this.base}/${id}`);
  }

  create(dto: IngredientRequest): Observable<IngredientResponse> {
    return this.http.post<IngredientResponse>(this.base, dto);
  }

  update(
    id: number,
    dto: IngredientUpdateRequest,
  ): Observable<IngredientResponse> {
    return this.http.put<IngredientResponse>(`${this.base}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  findByType(
    type: IngredientType,
    page = 0,
    size = 20,
  ): Observable<Page<IngredientResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<IngredientResponse>>(
      `${this.base}/type/${type}`,
      { params },
    );
  }
}
