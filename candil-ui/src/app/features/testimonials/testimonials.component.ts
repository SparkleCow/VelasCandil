import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RevealOnScrollDirective } from '../../shared/directives/reveal-on-scroll.directive';

interface Testimonial {
  text: string;
  name: string;
  initials: string;
  location: string;
}

@Component({
  selector: 'app-testimonials',
  standalone: true,
  imports: [MatIconModule, RevealOnScrollDirective],
  templateUrl: './testimonials.component.html',
  styleUrl: './testimonials.component.css',
})
export class TestimonialsComponent {
  private readonly base: Testimonial[] = [
    {
      text: 'El aroma de lavanda llenó toda mi sala en minutos. Una experiencia completamente relajante.',
      name: 'Ana Martínez',
      initials: 'AM',
      location: 'Bogotá',
    },
    {
      text: 'La vela de vainilla es increíble. La enciendo cada noche y dura muchísimo.',
      name: 'Camila Rodríguez',
      initials: 'CR',
      location: 'Medellín',
    },
    {
      text: 'Las regalé en un cumpleaños y todos quedaron encantados. El empaque es hermoso.',
      name: 'Laura Pérez',
      initials: 'LP',
      location: 'Cali',
    },
    {
      text: 'Compré la de sándalo y me transportó. Nunca había sentido algo así con una vela.',
      name: 'Juliana Soto',
      initials: 'JS',
      location: 'Bucaramanga',
    },
    {
      text: 'Producto artesanal de verdad. Se nota el cuidado en cada detalle. Volveré a comprar.',
      name: 'María Gómez',
      initials: 'MG',
      location: 'Barranquilla',
    },
    {
      text: 'El envío llegó súper rápido y bien protegido. La vela de jazmín es mi favorita.',
      name: 'Valeria Torres',
      initials: 'VT',
      location: 'Cartagena',
    },
  ];

  /** Duplicated once so the infinite marquee loops seamlessly at -50%. */
  readonly testimonials: Testimonial[] = [...this.base, ...this.base];
}
