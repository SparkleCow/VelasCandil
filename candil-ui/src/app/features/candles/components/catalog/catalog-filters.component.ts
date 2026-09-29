import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
} from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';

import {
  CATEGORIES,
  CategoryEnum,
  FEATURES,
  FeatureEnum,
  MATERIALS,
  MaterialEnum,
} from '../../../../shared/models/candle.models';
import {
  PRICE_SLIDER_MAX,
  SCENT_FAMILIES,
  ScentFamily,
} from './catalog.models';

export interface CatalogFilterControls {
  search: FormControl<string | null>;
  category: FormControl<CategoryEnum | ''>;
  material: FormControl<MaterialEnum | ''>;
  feature: FormControl<FeatureEnum | ''>;
  scentFamily: FormControl<ScentFamily | ''>;
  maxPrice: FormControl<number | null>;
}

export type CatalogFiltersForm = FormGroup<CatalogFilterControls>;

@Component({
  selector: 'app-catalog-filters',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatIconModule],
  templateUrl: './catalog-filters.component.html',
  styleUrl: './catalog-filters.component.css',
  host: {
    '(document:keydown.escape)': 'onEscapeKeydown()',
  },
})
export class CatalogFiltersComponent {
  readonly filtersForm = input.required<CatalogFiltersForm>();
  readonly open = input(false);
  readonly activeCount = input(0);

  readonly closed = output<void>();

  onEscapeKeydown(): void {
    if (this.open()) {
      this.closed.emit();
    }
  }

  readonly categories = CATEGORIES;
  readonly materials = MATERIALS;
  readonly features = FEATURES;
  readonly scentFamilies = SCENT_FAMILIES;
  readonly priceMax = PRICE_SLIDER_MAX;

  toggleCategory(value: CategoryEnum): void {
    const control = this.filtersForm().controls.category;
    control.setValue(control.value === value ? '' : value);
  }

  toggleScentFamily(value: ScentFamily): void {
    const control = this.filtersForm().controls.scentFamily;
    control.setValue(control.value === value ? '' : value);
  }

  clearAll(): void {
    this.filtersForm().patchValue({
      search: '',
      category: '',
      material: '',
      feature: '',
      scentFamily: '',
      maxPrice: null,
    });
  }
}
