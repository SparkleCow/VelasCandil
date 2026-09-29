import { Component, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { RevealOnScrollDirective } from '../../shared/directives/reveal-on-scroll.directive';

interface BrandStat {
  value: string;
  highlight?: string;
  label: string;
  description: string;
}

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [MatIconModule, RevealOnScrollDirective],
  templateUrl: './about.component.html',
  styleUrl: './about.component.css',
})
export class AboutComponent {
  private readonly router = inject(Router);

  readonly stats: BrandStat[] = [
    {
      value: '1',
      highlight: 'k+',
      label: 'Clientes felices',
      description: 'Hogares que ya iluminan con Candil',
    },
    {
      value: '30',
      highlight: '+',
      label: 'Fragancias',
      description: 'Aromas únicos creados con intención',
    },
    {
      value: '100',
      highlight: '%',
      label: 'Artesanal',
      description: 'Vertida a mano, una por una',
    },
  ];

  goToCatalog(): void {
    this.router.navigate(['/candles']);
  }
}
