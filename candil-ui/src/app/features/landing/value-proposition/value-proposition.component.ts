import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RevealOnScrollDirective } from '../../../shared/directives/reveal-on-scroll.directive';

interface ValuePillar {
  icon: string;
  title: string;
  description: string;
}

@Component({
  selector: 'app-value-proposition',
  standalone: true,
  imports: [MatIconModule, RevealOnScrollDirective],
  templateUrl: './value-proposition.component.html',
  styleUrl: './value-proposition.component.css',
})
export class ValuePropositionComponent {
  readonly pillars: ValuePillar[] = [
    {
      icon: 'eco',
      title: 'Cera de soya 100%',
      description:
        'Renovable, biodegradable y libre de parafina: una llama más limpia que cuida tu aire y al planeta.',
    },
    {
      icon: 'volunteer_activism',
      title: 'Vertidas a mano',
      description:
        'Cada vela nace en pequeños lotes, vaciada y sellada artesanalmente por manos que aman el detalle.',
    },
    {
      icon: 'spa',
      title: 'Aromaterapia premium',
      description:
        'Fragancias de grado fino diseñadas para calmar, energizar o envolver tu espacio según lo que necesites.',
    },
    {
      icon: 'schedule',
      title: 'Larga duración',
      description:
        'Hasta 40 horas de luz constante y aroma parejo, sin humo negro ni tunelado. El tiempo pasa, el encanto queda.',
    },
  ];
}
