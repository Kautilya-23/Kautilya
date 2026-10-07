import { Routes } from "@angular/router";
import { PatientsComponent } from "./pages/patients.component";
import { AppointmentsComponent } from "./pages/appointments.component";
import { ConsultationComponent } from "./pages/consultation.component";
export const routes: Routes = [
  { path: "", redirectTo: "patients", pathMatch: "full" },
  { path: "patients", component: PatientsComponent },
  { path: "appointments", component: AppointmentsComponent },
  { path: "consultation/:id", component: ConsultationComponent },
  { path: "**", redirectTo: "patients" }
];
