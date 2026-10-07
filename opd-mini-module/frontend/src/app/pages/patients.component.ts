import { Component, OnInit } from "@angular/core";
import { CommonModule, DatePipe } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { HttpErrorResponse } from "@angular/common/http";
import { Consultation, Patient } from "../models/models";
import { PatientService, errorMessage } from "../services/api.service";

@Component({ selector: "app-patients", standalone: true, imports: [CommonModule, FormsModule, DatePipe],
  template: `
  <div class="card"><h2>Register patient</h2>
    <form #f="ngForm" (ngSubmit)="register(f)" class="row">
      <label>Name<input name="name" [(ngModel)]="form.name" required minlength="2" maxlength="100" #n="ngModel"><span class="err" *ngIf="n.invalid && n.touched">Name required (min 2 chars)</span></label>
      <label>Gender<select name="gender" [(ngModel)]="form.gender" required><option value="">Select</option><option>MALE</option><option>FEMALE</option><option>OTHER</option></select></label>
      <label>Age<input type="number" name="age" [(ngModel)]="form.age" required min="0" max="120" #a="ngModel"><span class="err" *ngIf="a.invalid && a.touched">Age 0-120</span></label>
      <label>Phone<input name="phone" [(ngModel)]="form.phoneNumber" required pattern="[0-9]{10}" #p="ngModel"><span class="err" *ngIf="p.invalid && p.touched">10-digit number</span></label>
      <label>&nbsp;<button [disabled]="f.invalid || saving">{{ saving ? "Saving..." : "Register" }}</button></label>
    </form>
    <p class="msg ok" *ngIf="success">{{ success }}</p><p class="msg bad" *ngIf="error">{{ error }}</p></div>
  <div class="card"><h2>Patients</h2>
    <div class="row"><label><input placeholder="Search by name or phone" [(ngModel)]="q" (ngModelChange)="load()"></label></div>
    <p class="empty" *ngIf="loading">Loading...</p>
    <p class="empty" *ngIf="!loading && !patients.length">No patients found.</p>
    <table *ngIf="patients.length"><tr><th>Name</th><th>Gender</th><th>Age</th><th>Phone</th><th></th></tr>
      <tr *ngFor="let p of patients"><td>{{p.name}}</td><td>{{p.gender}}</td><td>{{p.age}}</td><td>{{p.phoneNumber}}</td>
        <td><button class="sec" (click)="showHistory(p)">History</button></td></tr></table></div>
  <div class="card" *ngIf="selected"><h2>Completed consultations — {{selected.name}}</h2>
    <p class="empty" *ngIf="!history.length">No completed consultations yet.</p>
    <table *ngIf="history.length"><tr><th>Date</th><th>Doctor</th><th>BP</th><th>Temp (°F)</th><th>Notes</th></tr>
      <tr *ngFor="let c of history"><td>{{c.completedAt | date:"medium"}}</td><td>{{c.doctorName}}</td><td>{{c.bloodPressure}}</td><td>{{c.temperature}}</td><td>{{c.notes}}</td></tr></table></div>` })
export class PatientsComponent implements OnInit {
  form = { name: "", gender: "", age: null as number | null, phoneNumber: "" };
  patients: Patient[] = []; history: Consultation[] = []; selected?: Patient;
  q = ""; loading = false; saving = false; success = ""; error = "";
  constructor(private svc: PatientService) {}
  ngOnInit() { this.load(); }
  load() {
    this.loading = true;
    this.svc.list(this.q).subscribe({ next: d => { this.patients = d; this.loading = false; }, error: (e: HttpErrorResponse) => { this.error = errorMessage(e); this.loading = false; } });
  }
  register(f: any) {
    this.saving = true; this.success = this.error = "";
    this.svc.create(this.form as any).subscribe({
      next: p => { this.success = "Patient " + p.name + " registered."; this.saving = false; f.resetForm({ gender: "" }); this.load(); },
      error: (e: HttpErrorResponse) => { this.error = errorMessage(e); this.saving = false; } });
  }
  showHistory(p: Patient) { this.selected = p; this.svc.history(p.id).subscribe({ next: h => this.history = h, error: (e: HttpErrorResponse) => this.error = errorMessage(e) }); }
}
