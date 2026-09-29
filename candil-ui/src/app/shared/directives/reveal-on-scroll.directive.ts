import {
  Directive,
  ElementRef,
  OnDestroy,
  OnInit,
  inject,
  input,
} from '@angular/core';

/**
 * Adds a smooth scroll-reveal animation when the host element
 * enters the viewport. Pairs with the global `.reveal` styles.
 *
 * Usage:
 *   <section appRevealOnScroll>…</section>
 *   <div appRevealOnScroll [revealDelay]="120">…</div>
 */
@Directive({
  selector: '[appRevealOnScroll]',
  standalone: true,
})
export class RevealOnScrollDirective implements OnInit, OnDestroy {
  /** Stagger delay in ms before the reveal animation starts. */
  readonly revealDelay = input(0);

  private readonly el = inject(ElementRef<HTMLElement>);
  private observer?: IntersectionObserver;

  ngOnInit(): void {
    const node = this.el.nativeElement as HTMLElement;
    node.classList.add('reveal');

    if (typeof IntersectionObserver === 'undefined') {
      node.classList.add('is-visible');
      return;
    }

    this.observer = new IntersectionObserver(
      (entries) => {
        for (const entry of entries) {
          if (entry.isIntersecting) {
            node.style.animationDelay = `${this.revealDelay()}ms`;
            node.classList.add('is-visible');
            this.observer?.unobserve(entry.target);
          }
        }
      },
      { threshold: 0.15, rootMargin: '0px 0px -40px 0px' },
    );

    this.observer.observe(node);
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
  }
}
