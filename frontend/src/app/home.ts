import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ClerkService } from 'ngx-clerk';

@Component({
  selector: 'app-home',
  template: `
    <section>
      <h2>Welcome to Enishi</h2>
      <p>You are signed in.</p>

      <button type="button" (click)="signOut()">
        Sign out
      </button>
    </section>
  `,
})
export class Home {
  private readonly clerk = inject(ClerkService);
  private readonly router = inject(Router);

  async signOut(): Promise<void> {
    await this.clerk.signOut();
    await this.router.navigateByUrl('/sign-in');
  }
}
