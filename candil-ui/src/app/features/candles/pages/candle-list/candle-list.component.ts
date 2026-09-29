import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import {
  combineLatest,
  debounceTime,
  distinctUntilChanged,
  map,
  startWith,
} from 'rxjs';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
} from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { CandleService } from '../../../../core/services/candle.service';
import { CartService } from '../../../../core/services/cart.service';
import { AuthService } from '../../../../core/services/auth.service';

import {
  CategoryEnum,
  CandleResponse,
  FeatureEnum,
  MaterialEnum,
} from '../../../../shared/models/candle.models';

import { environment } from '../../../../../environments/environment.development';
import { CatalogHeaderComponent } from '../../components/catalog/catalog-header.component';
import {
  CatalogFilterControls,
  CatalogFiltersForm,
  CatalogFiltersComponent,
} from '../../components/catalog/catalog-filters.component';
import { ProductCardComponent } from '../../components/catalog/product-card.component';
import { CatalogSkeletonComponent } from '../../components/catalog/catalog-skeleton.component';
import { CatalogEmptyStateComponent } from '../../components/catalog/catalog-empty-state.component';
import {
  detectScentFamily,
  ScentFamily,
  SortKey,
} from '../../components/catalog/catalog.models';

const WISHLIST_KEY = 'candil-wishlist';

@Component({
  selector: 'app-candle-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    MatIconModule,
    MatPaginatorModule,
    MatSnackBarModule,
    CatalogHeaderComponent,
    CatalogFiltersComponent,
    ProductCardComponent,
    CatalogSkeletonComponent,
    CatalogEmptyStateComponent,
  ],
  templateUrl: './candle-list.component.html',
  styleUrl: './candle-list.component.css',
})
export class CandleListComponent implements OnInit {
  private readonly candleService = inject(CandleService);
  private readonly cartService = inject(CartService);
  private readonly authService = inject(AuthService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);
  private readonly base = `${environment.apiBaseUrl}`;

  readonly candles = signal<CandleResponse[]>([]);
  readonly loading = signal(true);
  readonly totalElements = signal(0);
  readonly drawerOpen = signal(false);
  readonly addingId = signal<number | null>(null);
  readonly sort = signal<SortKey>('popular');
  readonly density = signal<'cozy' | 'comfy'>('comfy');
  readonly wishlist = signal<Set<number>>(this.loadWishlist());

  private readonly queryFilter = signal('');
  private readonly categoryFilter = signal<CategoryEnum | ''>('');
  private readonly materialFilter = signal<MaterialEnum | ''>('');
  private readonly featureFilter = signal<FeatureEnum | ''>('');
  private readonly scentFamilyFilter = signal<ScentFamily | ''>('');
  private readonly maxPriceFilter = signal<number | null>(null);

  pageSize = 12;
  currentPage = 0;

  readonly form: CatalogFiltersForm = new FormGroup<CatalogFilterControls>({
    search: new FormControl<string | null>(''),
    category: new FormControl<CategoryEnum | ''>('', { nonNullable: true }),
    material: new FormControl<MaterialEnum | ''>('', { nonNullable: true }),
    feature: new FormControl<FeatureEnum | ''>('', { nonNullable: true }),
    scentFamily: new FormControl<ScentFamily | ''>('', { nonNullable: true }),
    maxPrice: new FormControl<number | null>(null),
  });

  readonly visibleCandles = computed(() => {
    const family = this.scentFamilyFilter();
    const maxPrice = this.maxPriceFilter();
    const query = this.queryFilter();

    return this.candles()
      .filter((candle) => {
        const matchesFamily =
          !family || detectScentFamily(candle.name, candle.description) === family;
        const matchesPrice = maxPrice === null || candle.price <= maxPrice;
        const haystack = `${candle.name} ${candle.description}`.toLowerCase();
        const matchesQuery = !query || haystack.includes(query);

        return matchesFamily && matchesPrice && matchesQuery;
      })
      .sort(SORTERS[this.sort()]);
  });

  readonly activeFilterCount = computed(
    () =>
      [
        this.queryFilter(),
        this.categoryFilter(),
        this.materialFilter(),
        this.featureFilter(),
        this.scentFamilyFilter(),
        this.maxPriceFilter() !== null ? 'price' : '',
      ].filter(Boolean).length,
  );

  ngOnInit(): void {
    this.load();
    this.syncFilterSignals();

    combineLatest([
      this.form.controls.search.valueChanges.pipe(startWith(null)),
      this.form.controls.category.valueChanges.pipe(startWith(null)),
      this.form.controls.material.valueChanges.pipe(startWith(null)),
      this.form.controls.feature.valueChanges.pipe(startWith(null)),
    ])
      .pipe(
        map(() =>
          JSON.stringify([
            this.form.controls.search.value,
            this.form.controls.category.value,
            this.form.controls.material.value,
            this.form.controls.feature.value,
          ]),
        ),
        distinctUntilChanged(),
        debounceTime(350),
      )
      .subscribe(() => {
        this.currentPage = 0;
        this.load();
      });
  }

  private syncFilterSignals(): void {
    this.form.controls.search.valueChanges.subscribe((value) => {
      this.queryFilter.set(value?.trim().toLowerCase() ?? '');
    });
    this.form.controls.category.valueChanges.subscribe((value) => {
      this.categoryFilter.set(value);
    });
    this.form.controls.material.valueChanges.subscribe((value) => {
      this.materialFilter.set(value);
    });
    this.form.controls.feature.valueChanges.subscribe((value) => {
      this.featureFilter.set(value);
    });
    this.form.controls.scentFamily.valueChanges.subscribe((value) => {
      this.scentFamilyFilter.set(value);
    });
    this.form.controls.maxPrice.valueChanges.subscribe((value) => {
      this.maxPriceFilter.set(value);
    });
  }

  load(): void {
    this.loading.set(true);

    const search = this.form.controls.search.value?.trim() ?? '';
    const category = this.form.controls.category.value as CategoryEnum;
    const material = this.form.controls.material.value as MaterialEnum;
    const feature = this.form.controls.feature.value as FeatureEnum;

    const request$ = search
      ? this.candleService.search(search, this.currentPage, this.pageSize)
      : category
        ? this.candleService.findByCategory(category, this.currentPage, this.pageSize)
        : material
          ? this.candleService.findByMaterial(material, this.currentPage, this.pageSize)
          : feature
            ? this.candleService.findByFeature(feature, this.currentPage, this.pageSize)
            : this.candleService.findAll(this.currentPage, this.pageSize);

    request$.subscribe({
      next: (page) => {
        this.candles.set(page.content);
        this.totalElements.set(page.totalElements);
        this.loading.set(false);
      },
      error: () => {
        this.candles.set([]);
        this.loading.set(false);
      },
    });
  }

  onSortChange(sort: SortKey): void {
    this.sort.set(sort);
  }

  onDensityChange(density: 'cozy' | 'comfy'): void {
    this.density.set(density);
  }

  onPage(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.load();
  }

  clearFilters(): void {
    this.form.patchValue({
      search: '',
      category: '',
      material: '',
      feature: '',
      scentFamily: '',
      maxPrice: null,
    });
  }

  openDrawer(): void {
    this.drawerOpen.set(true);
  }

  closeDrawer(): void {
    this.drawerOpen.set(false);
  }

  isWishlisted(id: number): boolean {
    return this.wishlist().has(id);
  }

  toggleWishlist(id: number): void {
    const next = new Set(this.wishlist());

    if (next.has(id)) {
      next.delete(id);
    } else {
      next.add(id);
    }

    this.wishlist.set(next);
    localStorage.setItem(WISHLIST_KEY, JSON.stringify([...next]));
  }

  addToCart(candle: CandleResponse): void {
    if (!this.authService.logged()) {
      this.snackBar.open('Inicia sesión para agregar velas al carrito.', 'Login', {
        duration: 3500,
        horizontalPosition: 'end',
        verticalPosition: 'top',
      }).onAction().subscribe(() => this.router.navigate(['/login']));
      return;
    }

    if (candle.stock === 0 || this.addingId() !== null) {
      return;
    }

    this.addingId.set(candle.id);

    this.cartService.getOrCreateCart().subscribe({
      next: () => {
        this.cartService.addItem(candle.id).subscribe({
          next: () => {
            this.addingId.set(null);
            this.snackBar.open(`${candle.name} agregada al carrito.`, 'Ver carrito', {
              duration: 3000,
              horizontalPosition: 'end',
              verticalPosition: 'top',
            }).onAction().subscribe(() => this.router.navigate(['/cart']));
          },
          error: () => this.finishAddWithError(),
        });
      },
      error: () => this.finishAddWithError(),
    });
  }

  getImageUrl(path: string | null | undefined): string {
    if (!path) return '';

    if (path.startsWith('/images')) {
      return this.base + path;
    }

    return (
      'https://velas-candil-bucket-022374769637-us-east-2-an.s3.us-east-2.amazonaws.com/' +
      path
    );
  }

  private finishAddWithError(): void {
    this.addingId.set(null);
    this.snackBar.open('No pudimos agregar la vela. Inténtalo de nuevo.', 'Cerrar', {
      duration: 3000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
    });
  }

  private loadWishlist(): Set<number> {
    try {
      const raw = localStorage.getItem(WISHLIST_KEY);
      return raw ? new Set<number>(JSON.parse(raw)) : new Set<number>();
    } catch {
      return new Set<number>();
    }
  }
}

const SORTERS: Record<SortKey, (a: CandleResponse, b: CandleResponse) => number> = {
  popular: (a, b) => b.stock - a.stock,
  newest: (a, b) => b.id - a.id,
  price_asc: (a, b) => a.price - b.price,
  price_desc: (a, b) => b.price - a.price,
};
