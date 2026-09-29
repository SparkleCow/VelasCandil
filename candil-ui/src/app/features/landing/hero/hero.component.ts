import { Component, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';

@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [MatIconModule],
  templateUrl: './hero.component.html',
  styleUrl: './hero.component.css',
})
export class HeroComponent {
  private readonly router = inject(Router);

  goToCatalog(): void {
    this.router.navigate(['/candles']);
  }

  scrollToStory(): void {
    document
      .getElementById('nuestra-historia')
      ?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
}
