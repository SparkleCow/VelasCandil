import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterModule } from '@angular/router';

import { CandleService } from '../../../core/services/candle.service';
import {
  CandleResponse,
  CategoryEnum,
  CATEGORIES,
} from '../../../shared/models/candle.models';
import { RevealOnScrollDirective } from '../../../shared/directives/reveal-on-scroll.directive';
import { environment } from '../../../../environments/environment.development';

interface ShowcaseCandle {
  id?: number;
  name: string;
  description: string;
  price: number | null;
  image: string;
}

@Component({
  selector: 'app-featured-products',
  standalone: true,
  imports: [CommonModule, MatIconModule, RouterModule, RevealOnScrollDirective],
  templateUrl: './featured-products.component.html',
  styleUrl: './featured-products.component.css',
})
export class FeaturedProductsComponent implements OnInit {
  private readonly candleService = inject(CandleService);
  private readonly router = inject(Router);
  private readonly base = `${environment.apiBaseUrl}`;

  readonly products = signal<ShowcaseCandle[]>([]);

  private readonly curated: ShowcaseCandle[] = [
    {
      name: 'Vainilla & Ámbar',
      description:
        'Crema dulce de vainilla envuelta en ámbar cálido. El abrazo que tu sala merece.',
      price: null,
      image:
        'https://images.unsplash.com/photo-1602523961358-f9f03dd557db?auto=format&fit=crop&w=900&q=80',
    },
    {
      name: 'Lavanda & Salvia',
      description:
        'Un soplo de campo al atardecer para disolver el estrés del día.',
      price: null,
      image:
        'https://images.unsplash.com/photo-1512149177596-f817c7ef5d4c?auto=format&fit=crop&w=900&q=80',
    },
    {
      name: 'Sándalo Nocturno',
      description:
        'Madera ahumada y profundidad serena. Ideal para meditar o leer junto a la llama.',
      price: null,
      image:
        'https://images.unsplash.com/photo-1477120128765-a0528148fed2?auto=format&fit=crop&w=900&q=80',
    },
    {
      name: 'Cítricos del Trópico',
      description:
        'Naranja, bergamota y un toque de sol colombiano para despertar cualquier espacio.',
      price: null,
      image:
        'https://images.unsplash.com/photo-1594035910387-fea47794261f?auto=format&fit=crop&w=900&q=80',
    },
  ];

  ngOnInit(): void {
    this.candleService.findAll(0, 4).subscribe({
      next: (page) => {
        if (page.content.length > 0) {
          this.products.set(page.content.map((c) => this.toShowcase(c)));
        } else {
          this.fallbackToCurated();
        }
      },
      error: () => this.fallbackToCurated(),
    });
  }

  openDetail(product: ShowcaseCandle): void {
    if (product.id !== undefined) {
      this.router.navigate(['/candles', product.id]);
    } else {
      this.router.navigate(['/candles']);
    }
  }

  formatLabel(value: string): string {
    return CATEGORIES.find((c) => c.value === (value as CategoryEnum))?.label ?? value;
  }

  private toShowcase(candle: CandleResponse): ShowcaseCandle {
    return {
      id: candle.id,
      name: candle.name,
      description: candle.description,
      price: candle.price,
      image: this.getImageUrl(candle.principalImage),
    };
  }

  private fallbackToCurated(): void {
    this.products.set(this.curated);
  }

  private getImageUrl(path: string | null | undefined): string {
    if (!path) return '';
    if (path.startsWith('/images')) {
      return this.base + path;
    }
    return (
      'https://velas-candil-bucket-022374769637-us-east-2-an.s3.us-east-2.amazonaws.com/' +
      path
    );
  }
}
