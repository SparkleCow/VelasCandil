import { Component, computed, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';

import {
  CandleResponse,
  CATEGORIES,
  MATERIALS,
} from '../../../../shared/models/candle.models';
import {
  buildProductBadges,
  detectScentFamily,
  detectScentNote,
} from './catalog.models';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule, MatIconModule, RouterModule],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.css',
})
export class ProductCardComponent {
  readonly candle = input.required<CandleResponse>();
  readonly imageUrl = input('');
  readonly index = input(0);
  readonly wishlisted = input(false);
  readonly adding = input(false);

  readonly addToCart = output<void>();
  readonly toggleWishlist = output<number>();

  readonly badges = computed(() =>
    buildProductBadges(
      this.candle().materialEnums ?? [],
      this.candle().featureEnums ?? [],
      this.candle().stock,
    ),
  );

  readonly categoryLabel = computed(
    () =>
      CATEGORIES.find((c) => c.value === this.candle().categories?.[0])?.label ??
      null,
  );

  readonly materialLabel = computed(
    () =>
      MATERIALS.find((m) => m.value === this.candle().materialEnums?.[0])?.label ??
      null,
  );

  readonly scentNote = computed(() =>
    detectScentNote(this.candle().name, this.candle().description),
  );

  readonly soldOut = computed(() => this.candle().stock === 0);

  onWishlistToggle(): void {
    this.toggleWishlist.emit(this.candle().id);
  }
}
