import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ClerkService } from 'ngx-clerk';

@Component({
  imports: [RouterOutlet],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App {
  protected readonly clerk = inject(ClerkService);
}
