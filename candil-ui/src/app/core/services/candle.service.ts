import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CandleRequest,
  CandleUpdateRequest,
  CandleResponse,
  CategoryEnum,
  MaterialEnum,
  FeatureEnum,
  Page,
} from '../../shared/models/candle.models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CandleService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/v1/candles`;

  findAll(page = 0, size = 12): Observable<Page<CandleResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<CandleResponse>>(this.base, { params });
  }

  findById(id: number): Observable<CandleResponse> {
    return this.http.get<CandleResponse>(`${this.base}/${id}`);
  }

  create(
    dto: CandleRequest,
    principalImage: File,
    images: File[] = [],
  ): Observable<CandleResponse> {
    const formData = new FormData();
    formData.append(
      'data',
      new Blob([JSON.stringify(dto)], { type: 'application/json' }),
    );
    formData.append('principalImage', principalImage);
    images.forEach((image) => formData.append('images', image));

    return this.http.post<CandleResponse>(this.base, formData);
  }

  update(id: number, dto: CandleUpdateRequest): Observable<CandleResponse> {
    return this.http.put<CandleResponse>(`${this.base}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  findByCategory(
    category: CategoryEnum,
    page = 0,
    size = 12,
  ): Observable<Page<CandleResponse>> {
    const params = new HttpParams()
      .set('categoryEnum', category)
      .set('page', page)
      .set('size', size);
    return this.http.get<Page<CandleResponse>>(`${this.base}/category`, {
      params,
    });
  }

  findByMaterial(
    material: MaterialEnum,
    page = 0,
    size = 12,
  ): Observable<Page<CandleResponse>> {
    const params = new HttpParams()
      .set('materialEnum', material)
      .set('page', page)
      .set('size', size);
    return this.http.get<Page<CandleResponse>>(`${this.base}/material`, {
      params,
    });
  }

  findByFeature(
    feature: FeatureEnum,
    page = 0,
    size = 12,
  ): Observable<Page<CandleResponse>> {
    const params = new HttpParams()
      .set('featureEnum', feature)
      .set('page', page)
      .set('size', size);
    return this.http.get<Page<CandleResponse>>(`${this.base}/feature`, {
      params,
    });
  }

  search(name: string, page = 0, size = 12): Observable<Page<CandleResponse>> {
    const params = new HttpParams()
      .set('name', name)
      .set('page', page)
      .set('size', size);
    return this.http.get<Page<CandleResponse>>(`${this.base}/search`, {
      params,
    });
  }
}
