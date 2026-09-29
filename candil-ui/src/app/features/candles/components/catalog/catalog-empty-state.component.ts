import { Component, input, output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-catalog-empty-state',
  standalone: true,
  imports: [MatIconModule],
  template: `
    <div class="empty">
      <span class="empty__flame" aria-hidden="true"></span>
      <mat-icon class="empty__icon">search_off</mat-icon>
      <h3 class="empty__title">Ninguna vela por aquí…</h3>
      <p class="empty__text">
        {{ hasFilters()
          ? 'Ninguna vela coincide con tus filtros. Prueba ampliando el precio o cambiando la familia aromática.'
          : 'Aún no hay velas disponibles. Vuelve muy pronto.' }}
      </p>
      @if (hasFilters()) {
        <button class="empty__cta" type="button" (click)="clear.emit()">
          <mat-icon>restart_alt</mat-icon>
          Ver todas las velas
        </button>
      }
    </div>
  `,
  styles: `
    .empty {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10px;
      text-align: center;
      padding: clamp(56px, 9vw, 96px) 24px;
      border: 1px dashed var(--candil-sand-dark);
      border-radius: 16px;
      background:
        radial-gradient(ellipse 60% 55% at 50% 0%, rgba(212, 162, 78, 0.09), transparent),
        #fffefb;
      animation: empty-in 0.5s cubic-bezier(0.22, 0.61, 0.36, 1) both;
    }

    @keyframes empty-in {
      from {
        opacity: 0;
        transform: translateY(14px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .empty__flame {
      width: 14px;
      height: 20px;
      margin-bottom: -6px;
      border-radius: 50% 50% 50% 50% / 62% 62% 38% 38%;
      background: radial-gradient(circle at 50% 30%, #ffd98a, var(--candil-amber) 75%);
      box-shadow: 0 0 18px 5px rgba(212, 162, 78, 0.45);
      animation: candil-flicker 2.4s ease-in-out infinite;
    }

    .empty__icon {
      font-size: 44px !important;
      width: 44px !important;
      height: 44px !important;
      color: var(--candil-sand-dark);
    }

    .empty__title {
      font-family: var(--font-display);
      font-style: italic;
      font-weight: 400;
      font-size: 1.4rem;
      color: var(--candil-charcoal);
      margin: 4px 0 0;
    }

    .empty__text {
      max-width: 42ch;
      font-size: 0.88rem;
      font-weight: 300;
      line-height: 1.75;
      color: var(--candil-muted);
      margin: 0;
    }

    .empty__cta {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      margin-top: 16px;
      padding: 12px 26px;
      border: none;
      border-radius: 999px;
      background: linear-gradient(135deg, var(--candil-amber), var(--candil-terracotta));
      color: #fff;
      font-family: var(--font-body);
      font-size: 0.74rem;
      letter-spacing: 0.14em;
      text-transform: uppercase;
      cursor: pointer;
      box-shadow: var(--shadow-amber);
      transition: transform 0.25s ease, box-shadow 0.25s ease;
    }

    .empty__cta:hover {
      transform: translateY(-2px);
      box-shadow: 0 16px 34px rgba(184, 92, 56, 0.4);
    }

    .empty__cta mat-icon {
      font-size: 16px !important;
      width: 16px !important;
      height: 16px !important;
    }
  `,
})
export class CatalogEmptyStateComponent {
  readonly hasFilters = input(false);
  readonly clear = output<void>();
}
