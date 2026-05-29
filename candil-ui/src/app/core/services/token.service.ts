import { Injectable } from '@angular/core';
import { jwtDecode, JwtPayload } from 'jwt-decode';

@Injectable({
  providedIn: 'root',
})
export class TokenService {
  private readonly key = 'jwt';

  get(): string | null {
    return localStorage.getItem(this.key);
  }

  set(token: string): void {
    localStorage.setItem(this.key, token);
  }

  remove(): void {
    localStorage.removeItem(this.key);
  }

  // Checks if the user is logged in by validating the JWT token
  isLogged(): boolean {
    const token = this.get();
    if (!token) return false;

    try {
      // Decodes the JWT to access its payload (no secret key needed)
      const decoded = jwtDecode<JwtPayload>(token);

      // Current time in seconds (JWT exp is also in seconds)
      const now = Math.floor(Date.now() / 1000);

      // If token has no expiration or is expired, user is not logged in
      if (!decoded.exp) return false;

      // Token is valid only if expiration time is in the future
      return decoded.exp > now;
    } catch {
      // If token is malformed or invalid, treat as not logged in
      return false;
    }
  }
}
