import { inject, Service } from '@angular/core';
import { ClerkService } from 'ngx-clerk';

@Service()
export class ClerkTokenService {
  private readonly clerk = inject(ClerkService);

  async getSessionToken(): Promise<string | null> {
    if (!this.clerk.isLoaded() || !this.clerk.isSignedIn()) {
      return null;
    }

    return this.clerk.getToken();
  }
}
