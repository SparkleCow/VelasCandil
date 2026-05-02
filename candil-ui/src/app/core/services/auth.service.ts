import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import {
  AuthLoginDto,
  AuthRegisterDto,
  AuthResponseDto,
} from '../../shared/models/auth.models';
import { TokenService } from './token.service';
import { UserInformation } from '../../shared/models/user-information.models';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenService = inject(TokenService);
  private readonly baseUrl = 'http://localhost:8080/v1/auth';

  register(data: AuthRegisterDto): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/register`, data);
  }

  login(data: AuthLoginDto): Observable<AuthResponseDto> {
    return this.http.post<AuthResponseDto>(`${this.baseUrl}/login`, data).pipe(
      tap((response) => {
        this.tokenService.set(response.jwt);
      }),
    );
  }

  activate(token: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/activate?token=${token}`, {});
  }

  logout(): void {
    this.tokenService.remove();
  }
}
