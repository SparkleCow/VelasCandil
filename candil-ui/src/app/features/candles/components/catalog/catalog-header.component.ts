import { Component, input, output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RevealOnScrollDirective } from '../../../../shared/directives/reveal-on-scroll.directive';
import { SORT_OPTIONS, SortKey } from './catalog.models';

@Component({
  selector: 'app-catalog-header',
  standalone: true,
  imports: [MatIconModule, RevealOnScrollDirective],
  templateUrl: './catalog-header.component.html',
  styleUrl: './catalog-header.component.css',
})
export class CatalogHeaderComponent {
  readonly shown = input(0);
  readonly total = input(0);
  readonly sort = input.required<SortKey>();
  readonly density = input.required<'cozy' | 'comfy'>();

  readonly sortChange = output<SortKey>();
  readonly densityChange = output<'cozy' | 'comfy'>();

  readonly sortOptions = SORT_OPTIONS;

  onSortChange(event: Event): void {
    this.sortChange.emit((event.target as HTMLSelectElement).value as SortKey);
  }
}
