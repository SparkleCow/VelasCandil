import { Component } from '@angular/core';
import { HeroComponent } from '../../../landing/hero/hero.component';
import { ValuePropositionComponent } from '../../../landing/value-proposition/value-proposition.component';
import { FeaturedProductsComponent } from '../../../landing/featured-products/featured-products.component';
import { ScentGuideComponent } from '../../../landing/scent-guide/scent-guide.component';
import { AboutComponent } from '../../../about/about.component';
import { TestimonialsComponent } from '../../../testimonials/testimonials.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    HeroComponent,
    ValuePropositionComponent,
    FeaturedProductsComponent,
    AboutComponent,
    ScentGuideComponent,
    TestimonialsComponent,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {}
