import { Routes } from '@angular/router';
import {
  canActivateClerk,
  catchAllRoute,
  ClerkSignInComponent,
  ClerkSignUpComponent,
} from 'ngx-clerk';

export const routes: Routes = [
  {
    path:"",
    redirectTo: '/home',
    pathMatch: 'full'
  },
  {
    matcher: catchAllRoute('sign-in'),
    component: ClerkSignInComponent,
  },
  {
    matcher: catchAllRoute('sign-up'),
    component: ClerkSignUpComponent,
  },
  {
    path: 'home',
    canActivate: [canActivateClerk],
    loadComponent: () =>
      import('./home').then((m) => m.Home),
  }
];
