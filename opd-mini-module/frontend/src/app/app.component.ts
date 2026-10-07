import { Component } from "@angular/core";
import { RouterLink, RouterLinkActive, RouterOutlet } from "@angular/router";
@Component({ selector: "app-root", standalone: true, imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `<header><h1>OPD Mini Module</h1>
    <a routerLink="/patients" routerLinkActive="active">Patients</a>
    <a routerLink="/appointments" routerLinkActive="active">Appointments</a></header>
    <main><router-outlet /></main>` })
export class AppComponent {}
