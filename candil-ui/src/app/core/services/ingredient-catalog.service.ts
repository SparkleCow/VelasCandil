import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment.development';
import { IngredientCatalogResponse } from '../../shared/models/ingredient.models';

@Injectable({
  providedIn: 'root',
})
export class IngredientCatalogService {
  private readonly http = inject(HttpClient);

  private readonly api = environment.apiBaseUrl + '/v1/catalog';

  findAll() {
    return this.http.get<IngredientCatalogResponse[]>(this.api);
  }
}
