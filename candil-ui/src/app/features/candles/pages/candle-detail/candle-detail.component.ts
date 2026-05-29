import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CandleService } from '../../../../core/services/candle.service';
import { CartService } from '../../../../core/services/cart.service';
import { CandleResponse } from '../../../../shared/models/candle.models';
import { environment } from '../../../../../environments/environment.development';

@Component({
  selector: 'app-candle-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
  ],
  templateUrl: './candle-detail.component.html',
  styleUrl: './candle-detail.component.css',
})
export class CandleDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly candleService = inject(CandleService);
  private readonly cartService = inject(CartService);
  private readonly snackBar = inject(MatSnackBar);
  readonly base = `${environment.apiBaseUrl}`;

  candle = signal<CandleResponse | null>(null);
  loading = signal(true);
  addingToCart = signal(false);
  selectedImage = signal<string | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.candleService.findById(id).subscribe({
      next: (c) => {
        console.log(c);
        this.candle.set(c);
        this.selectedImage.set(c.principalImage ?? null);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.router.navigate(['/candles']);
      },
    });
  }

  selectImage(img: string): void {
    this.selectedImage.set(img);
  }

  addToCart(): void {
    const c = this.candle();
    if (!c || c.stock === 0) return;

    this.addingToCart.set(true);

    // Primero asegurar que el carrito existe, luego agregar el item
    this.cartService.getOrCreateCart().subscribe({
      next: () => {
        this.cartService.addItem(c.id).subscribe({
          next: () => {
            this.addingToCart.set(false);
            this.snackBar
              .open('Vela agregada al carrito', 'Ver carrito', {
                duration: 3000,
                horizontalPosition: 'end',
                verticalPosition: 'top',
              })
              .onAction()
              .subscribe(() => this.router.navigate(['/cart']));
          },
          error: () => {
            this.addingToCart.set(false);
            this.router.navigate(['/cart']);
          },
        });
      },
      error: () => {
        this.addingToCart.set(false);
        this.router.navigate(['/cart']);
      },
    });
  }

  formatLabel(value: string): string {
    return value.replace(/_/g, ' ');
  }

  getAllImages(): string[] {
    const c = this.candle();
    if (!c) return [];
    const extras = c.images ?? [];
    return c.principalImage ? [c.principalImage, ...extras] : extras;
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
