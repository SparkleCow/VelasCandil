import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { RevealOnScrollDirective } from '../../../shared/directives/reveal-on-scroll.directive';

interface ScentFamily {
  icon: string;
  name: string;
  notes: string;
  mood: string;
  accent: string;
}

@Component({
  selector: 'app-scent-guide',
  standalone: true,
  imports: [MatIconModule, RevealOnScrollDirective],
  templateUrl: './scent-guide.component.html',
  styleUrl: './scent-guide.component.css',
})
export class ScentGuideComponent {
  private readonly router = inject(Router);

  readonly scents: ScentFamily[] = [
    {
      icon: 'nightlight',
      name: 'Relajantes',
      notes: 'Lavanda · Manzanilla · Salvia blanca',
      mood: 'Para soltar el día y dormir profundo.',
      accent: '#8f7bb5',
    },
    {
      icon: 'local_florist',
      name: 'Florales',
      notes: 'Jazmín · Rosa · Geranio',
      mood: 'Para llenar la casa de primavera.',
      accent: '#c98a9e',
    },
    {
      icon: 'forest',
      name: 'Amaderados',
      notes: 'Sándalo · Cedro · Vetiver',
      mood: 'Para leer, meditar y reconectar.',
      accent: '#a07840',
    },
    {
      icon: 'wb_sunny',
      name: 'Cítricos',
      notes: 'Bergamota · Naranja · Limón',
      mood: 'Para despertar la energía del espacio.',
      accent: '#d4a24e',
    },
    {
      icon: 'bakery_dining',
      name: 'Dulces',
      notes: 'Vainilla · Canela · Caramelo',
      mood: 'Para abrazos, pan recién horneado y hogar.',
      accent: '#b85c38',
    },
    {
      icon: 'self_improvement',
      name: 'Aromaterapia',
      notes: 'Eucalipto · Menta · Árbol de té',
      mood: 'Para respirar hondo y renovarte.',
      accent: '#6f9e8a',
    },
  ];

  explore(): void {
    this.router.navigate(['/candles']);
  }
}
