import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';

import { CandleService } from '../../../../core/services/candle.service';
import { NavbarComponent } from '../../../../shared/components/navbar/navbar.component';
import {
  CandleResponse,
  CategoryEnum,
  MaterialEnum,
  FeatureEnum,
  CATEGORIES,
  MATERIALS,
  FEATURES,
} from '../../../../shared/models/candle.models';
import { environment } from '../../../../../environments/environment.development';

@Component({
  selector: 'app-candle-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatChipsModule,
  ],
  templateUrl: './candle-list.component.html',
  styleUrl: './candle-list.component.css',
})
export class CandleListComponent implements OnInit {
  private readonly candleService = inject(CandleService);
  private readonly base = `${environment.apiBaseUrl}`;

  candles = signal<CandleResponse[]>([]);
  loading = signal(false);
  totalElements = signal(0);
  pageSize = 12;
  currentPage = 0;
  showFilters = false;

  searchCtrl = new FormControl('');
  categoryCtrl = new FormControl<CategoryEnum | ''>('');
  materialCtrl = new FormControl<MaterialEnum | ''>('');
  featureCtrl = new FormControl<FeatureEnum | ''>('');

  readonly categories = CATEGORIES;
  readonly materials = MATERIALS;
  readonly features = FEATURES;

  ngOnInit(): void {
    this.load();

    this.searchCtrl.valueChanges
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe(() => {
        this.currentPage = 0;
        this.load();
      });

    this.categoryCtrl.valueChanges.subscribe(() => {
      this.currentPage = 0;
      this.load();
    });
    this.materialCtrl.valueChanges.subscribe(() => {
      this.currentPage = 0;
      this.load();
    });
    this.featureCtrl.valueChanges.subscribe(() => {
      this.currentPage = 0;
      this.load();
    });
  }

  load(): void {
    this.loading.set(true);
    const search = this.searchCtrl.value?.trim() ?? '';
    const category = this.categoryCtrl.value as CategoryEnum;
    const material = this.materialCtrl.value as MaterialEnum;
    const feature = this.featureCtrl.value as FeatureEnum;

    const obs = search
      ? this.candleService.search(search, this.currentPage, this.pageSize)
      : category
        ? this.candleService.findByCategory(
            category,
            this.currentPage,
            this.pageSize,
          )
        : material
          ? this.candleService.findByMaterial(
              material,
              this.currentPage,
              this.pageSize,
            )
          : feature
            ? this.candleService.findByFeature(
                feature,
                this.currentPage,
                this.pageSize,
              )
            : this.candleService.findAll(this.currentPage, this.pageSize);

    obs.subscribe({
      next: (page) => {
        this.candles.set(page.content);
        this.totalElements.set(page.totalElements);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  onPage(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.load();
  }

  clearFilters(): void {
    this.searchCtrl.setValue('');
    this.categoryCtrl.setValue('');
    this.materialCtrl.setValue('');
    this.featureCtrl.setValue('');
  }

  hasFilters(): boolean {
    return !!(
      this.searchCtrl.value ||
      this.categoryCtrl.value ||
      this.materialCtrl.value ||
      this.featureCtrl.value
    );
  }

  formatLabel(value: string): string {
    return value.replace(/_/g, ' ');
  }

  getImageUrl(path: string | null | undefined): string {
    if (!path) return '';

    // (local backend)
    if (path.startsWith('/images')) {
      return this.base + path;
    }

    // (S3 key)
    return (
      'https://velas-candil-bucket-022374769637-us-east-2-an.s3.us-east-2.amazonaws.com/' +
      path
    );
  }
}
