import { Component, input } from '@angular/core';

@Component({
  selector: 'app-catalog-skeleton',
  standalone: true,
  template: `
    <div class="skeleton-grid" aria-hidden="true">
      @for (i of skeletonItems; track i) {
        <div class="skel-card">
          <div class="skel-media shimmer"></div>
          <div class="skel-body">
            <span class="skel-line shimmer" style="width: 42%"></span>
            <span class="skel-line skel-line--title shimmer"></span>
            <span class="skel-line shimmer" style="width: 58%"></span>
            <span class="skel-line shimmer" style="width: 88%"></span>
            <div class="skel-footer">
              <span class="skel-line shimmer" style="width: 30%"></span>
              <span class="skel-btn shimmer"></span>
            </div>
          </div>
        </div>
      }
    </div>
    <p class="sr-only">Cargando velas…</p>
  `,
  styles: `
    .skeleton-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 24px;
    }

    .skel-card {
      background: var(--candil-surface);
      border: 1px solid var(--candil-sand);
      border-radius: 14px;
      overflow: hidden;
    }

    .skel-media {
      aspect-ratio: 4 / 3.4;
    }

    .skel-body {
      display: flex;
      flex-direction: column;
      gap: 11px;
      padding: 17px;
    }

    .skel-line {
      display: block;
      height: 11px;
      border-radius: 999px;
    }

    .skel-line--title {
      height: 17px;
      width: 70%;
    }

    .skel-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 10px;
      padding-top: 14px;
      border-top: 1px solid var(--candil-vanilla);
    }

    .skel-btn {
      width: 118px;
      height: 40px;
      border-radius: 999px;
    }

    .shimmer {
      background: linear-gradient(
        100deg,
        var(--candil-vanilla) 35%,
        #fbf5ea 50%,
        var(--candil-vanilla) 65%
      );
      background-size: 220% 100%;
      animation: shimmer-sweep 1.5s ease-in-out infinite;
    }

    @keyframes shimmer-sweep {
      from {
        background-position: 130% 0;
      }
      to {
        background-position: -90% 0;
      }
    }

    .sr-only {
      position: absolute;
      width: 1px;
      height: 1px;
      overflow: hidden;
      clip-path: inset(50%);
    }

    @media (max-width: 1024px) {
      .skeleton-grid {
        grid-template-columns: repeat(2, 1fr);
      }
    }

    @media (max-width: 560px) {
      .skeleton-grid {
        grid-template-columns: 1fr;
      }
    }
  `,
})
export class CatalogSkeletonComponent {
  readonly count = input(6);

  get skeletonItems(): number[] {
    return Array.from({ length: this.count() }, (_, i) => i);
  }
}
