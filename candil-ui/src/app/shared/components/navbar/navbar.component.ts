import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthService } from '../../../core/services/auth.service';
import { UserInformation } from '../../models/user-information.models';
import { UserService } from '../../../core/services/user.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatTooltipModule,
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent implements OnInit {
  private authService = inject(AuthService);

  auth: boolean = false;
  username: String = '';
  imageUrl: String = '';
  isAdmin = false;
  isLogged = this.authService.logged;

  constructor(
    private userService: UserService,
    private router: Router,
  ) {
    effect(() => {
      if (this.isLogged()) {
        this.loadUser();
      } else {
        this.username = '';
        this.imageUrl = '';
        this.isAdmin = false;
      }
    });
  }

  ngOnInit(): void {
    if (this.isLogged()) {
      this.loadUser();
    }

    const saved = localStorage.getItem('theme');
    const prefersDark = window.matchMedia(
      '(prefers-color-scheme: dark)',
    ).matches;
    const dark = saved ? saved === 'dark' : prefersDark;
    this.isDark.set(dark);
    this.applyTheme(dark);
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  isDark = signal(false);

  toggleTheme(): void {
    const next = !this.isDark();
    this.isDark.set(next);
    localStorage.setItem('theme', next ? 'dark' : 'light');
    this.applyTheme(next);
  }

  private loadUser(): void {
    this.userService.getUserInformation().subscribe({
      next: (response: UserInformation) => {
        this.username = response.username;
        this.imageUrl = response.imageUrl;
        this.isAdmin = this.hasAdminRole(response.roles ?? []);
      },
      error: () => {
        this.authService.setLogged(false);
      },
    });
  }

  private applyTheme(dark: boolean): void {
    const body = document.body;
    if (dark) {
      body.classList.remove('light-theme');
      body.classList.add('dark-theme');
    } else {
      body.classList.remove('dark-theme');
      body.classList.add('light-theme');
    }
  }

  logout(): void {
    this.authService.logout();
    this.auth = false;
    this.isAdmin = false;
    this.router.navigate(['/']);
  }

  register(): void {
    this.router.navigate(['/register']);
  }

  cart(): void {
    this.router.navigate(['/cart']);
  }

  profile(): void {
    this.router.navigate(['/profile']);
  }

  purchases(): void {
    this.router.navigate(['/ordes']);
  }

  createCandle(): void {
    this.router.navigate(['/candles/create']);
  }

  contact(): void {
    this.router.navigate(['/contact']);
  }

  information(): void {
    this.router.navigate(['/information']);
  }

  catalog(): void {
    this.router.navigate(['/candles']);
  }

  home(): void {
    this.router.navigate(['/']);
  }

  private hasAdminRole(roles: Array<string | { authority?: string }>): boolean {
    return roles.some((role) => {
      if (typeof role === 'string') {
        return role === 'ADMIN' || role === 'ROLE_ADMIN';
      }

      return role.authority === 'ADMIN' || role.authority === 'ROLE_ADMIN';
    });
  }
}
