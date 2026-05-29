import { Injectable, inject } from '@angular/core';
import { TokenService } from './token.service';
import { Observable } from 'rxjs';
import { UserInformation } from '../../shared/models/user-information.models';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly tokenService = inject(TokenService);
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/v1/user`;

  private decodePayload(): Record<string, unknown> | null {
    const token = this.tokenService.get();
    if (!token) return null;
    try {
      const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(atob(base64));
    } catch {
      return null;
    }
  }

  getUsername(): string {
    return (this.decodePayload()?.['sub'] as string) ?? '';
  }

  getRoles(): string[] {
    const payload = this.decodePayload();
    if (!payload) return [];

    const raw = payload['authorities'] ?? payload['roles'] ?? [];
    if (!Array.isArray(raw)) return [];

    return (raw as unknown[])
      .map((a) => {
        if (typeof a === 'string') return a;
        if (typeof a === 'object' && a && 'authority' in a) {
          return (a as { authority: string }).authority;
        }
        return '';
      })
      .filter(Boolean);
  }

  isAdmin(): boolean {
    return this.getRoles().some((r) => r === 'ADMIN' || r === 'ROLE_ADMIN');
  }

  isLogged(): boolean {
    return this.tokenService.isLogged();
  }

  getUserInformation(): Observable<UserInformation> {
    return this.http.get<UserInformation>(`${this.baseUrl}/self`);
  }

  updateUsername(username: string): Observable<UserInformation> {
    return this.http.patch<UserInformation>(`${this.baseUrl}/self/username`, {
      username,
    });
  }

  updateProfileImage(file: File, key: string): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.put(
      `${this.baseUrl}/upload?key=${encodeURIComponent(key)}`,
      formData,
      {
        responseType: 'text',
      },
    );
  }
}
