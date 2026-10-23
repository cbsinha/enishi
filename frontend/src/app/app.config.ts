import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideClerk } from 'ngx-clerk';
import { environment } from '../environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideClerk({
      publishableKey: environment.clerkPublishableKey,
      signInUrl: '/sign-in',
      signUpUrl: '/sign-up'
    })
  ]
};
