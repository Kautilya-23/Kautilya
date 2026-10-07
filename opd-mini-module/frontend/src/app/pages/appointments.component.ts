import { Component, OnInit } from "@angular/core";
import { CommonModule, DatePipe } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import { HttpErrorResponse } from "@angular/common/http";
import { Appointment, Doctor, Patient } from "../models/models";
import { AppointmentService, PatientService, errorMessage } from "../services/api.service";

@Component({ selector: "app-appointments", standalone: true, imports: [CommonModule, FormsModule, DatePipe],
  template: `
  <div class="card"><h2>Book appointment</h2>
    <form #f="ngForm" (ngSubmit)="book(f)" class="row">
      <label>Patient<select name="p" [(ngModel)]="patientId" required><option [ngValue]="null">Select patient</option><option *ngFor="let p of patients" [ngValue]="p.id">{{p.name}} ({{p.phoneNumber}})</option></select></label>
      <label>Doctor<select name="d" [(ngModel)]="doctorId" required><option [ngValue]="null">Select doctor</option><option *ngFor="let d of doctors" [ngValue]="d.id">{{d.name}} — {{d.specialization}}</option></select></label>
      <label>Date &amp; time<input type="datetime-local" name="dt" [(ngModel)]="dateTime" required></label>
      <label>&nbsp;<button [disabled]="f.invalid || saving">Book</button></label>
    </form>
    <p class="msg ok" *ngIf="success">{{ success }}</p><p class="msg bad" *ngIf="error">{{ error }}</p></div>
  <div class="card"><h2>Today&#39;s appointments</h2>
    <p class="empty" *ngIf="loading">Loading...</p>
    <p class="empty" *ngIf="!loading && !appts.length">No appointments today.</p>
    <table *ngIf="appts.length"><tr><th>Time</th><th>Patient</th><th>Doctor</th><th>Status</th><th></th></tr>
      <tr *ngFor="let a of appts"><td>{{a.appointmentDateTime | date:"shortTime"}}</td><td>{{a.patientName}}</td><td>{{a.doctorName}}</td>
        <td><span class="tag {{a.status}}">{{a.status}}</span></td>
        <td><button (click)="open(a)">{{ a.status === "BOOKED" ? "Open consultation" : "View" }}</button></td></tr></table></div>` })
export class AppointmentsComponent implements OnInit {
  patients: Patient[] = []; doctors: Doctor[] = []; appts: Appointment[] = [];
  patientId: number | null = null; doctorId: number | null = null; dateTime = "";
  loading = false; saving = false; success = ""; error = "";
  constructor(private ps: PatientService, private as: AppointmentService, private router: Router) {}
  ngOnInit() {
    this.ps.list().subscribe({ next: p => this.patients = p, error: (e: HttpErrorResponse) => this.error = errorMessage(e) });
    this.as.doctors().subscribe({ next: d => this.doctors = d, error: () => {} });
    this.load();
  }
  load() { this.loading = true; this.as.today().subscribe({ next: a => { this.appts = a; this.loading = false; }, error: (e: HttpErrorResponse) => { this.error = errorMessage(e); this.loading = false; } }); }
  book(f: any) {
    this.saving = true; this.success = this.error = "";
    this.as.book({ patientId: this.patientId!, doctorId: this.doctorId!, appointmentDateTime: this.dateTime.length === 16 ? this.dateTime + ":00" : this.dateTime }).subscribe({
      next: a => { const today = new Date().toDateString() === new Date(a.appointmentDateTime).toDateString();
      this.success = "Appointment booked for " + a.patientName + (today ? "." : " (not today, so it will not appear in today's list)."); this.saving = false; f.resetForm(); this.load(); },
      error: (e: HttpErrorResponse) => { this.error = errorMessage(e); this.saving = false; } });
  }
  open(a: Appointment) { this.router.navigate(["/consultation", a.id]); }
}
