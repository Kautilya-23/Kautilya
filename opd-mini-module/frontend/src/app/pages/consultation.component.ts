import { Component, OnInit } from "@angular/core";
import { CommonModule, DatePipe } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { HttpErrorResponse } from "@angular/common/http";
import { Appointment, Consultation } from "../models/models";
import { AppointmentService, ConsultationService, PatientService, errorMessage } from "../services/api.service";

@Component({ selector: "app-consultation", standalone: true, imports: [CommonModule, FormsModule, DatePipe, RouterLink],
  template: `
  <p class="msg bad" *ngIf="error">{{ error }}</p>
  <div class="card" *ngIf="appt"><h2>Consultation</h2>
    <p><b>Patient:</b> {{appt.patientName}} &nbsp; <b>Doctor:</b> {{appt.doctorName}} &nbsp; <b>When:</b> {{appt.appointmentDateTime | date:"medium"}} &nbsp; <span class="tag {{appt.status}}">{{appt.status}}</span></p>
    <form #f="ngForm" (ngSubmit)="complete()" class="row" *ngIf="!done">
      <label>Blood pressure<input name="bp" [(ngModel)]="bp" required pattern="\\d{2,3}/\\d{2,3}" placeholder="120/80" #b="ngModel"><span class="err" *ngIf="b.invalid && b.touched">Format 120/80</span></label>
      <label>Temperature (°F)<input type="number" step="0.1" name="t" [(ngModel)]="temp" required min="90" max="110"></label>
      <label style="flex-basis:100%">Notes<textarea name="n" rows="4" [(ngModel)]="notes" maxlength="1000"></textarea></label>
      <button [disabled]="f.invalid || saving">{{ saving ? "Saving..." : "Complete consultation" }}</button></form>
    <div *ngIf="done"><p class="msg ok">Consultation completed — this record is locked and cannot be completed again.</p>
      <p><b>BP:</b> {{bp}} &nbsp; <b>Temp:</b> {{temp}}°F</p><p><b>Notes:</b> {{notes || "—"}}</p></div>
    <p><button class="sec" type="button" routerLink="/appointments">Back to appointments</button></p></div>
  <div class="card" *ngIf="appt && done"><h2>Completed consultations — {{appt.patientName}}</h2>
    <p class="empty" *ngIf="!history.length">No completed consultations yet.</p>
    <table *ngIf="history.length"><tr><th>Completed</th><th>Doctor</th><th>BP</th><th>Temp (°F)</th><th>Notes</th></tr>
      <tr *ngFor="let c of history"><td>{{c.completedAt | date:"medium"}}</td><td>{{c.doctorName}}</td><td>{{c.bloodPressure}}</td><td>{{c.temperature}}</td><td>{{c.notes}}</td></tr></table></div>` })
export class ConsultationComponent implements OnInit {
  appt?: Appointment; history: Consultation[] = []; id = 0; bp = ""; temp: number | null = null; notes = ""; done = false; saving = false; error = "";
  constructor(private route: ActivatedRoute, private as: AppointmentService, private cs: ConsultationService, private ps: PatientService) {}
  ngOnInit() {
    this.id = Number(this.route.snapshot.paramMap.get("id"));
    this.as.get(this.id).subscribe({ next: a => { this.appt = a; this.done = a.status === "COMPLETED"; if (this.done) { this.cs.get(this.id).subscribe(c => this.fill(c)); this.loadHistory(); } }, error: (e: HttpErrorResponse) => this.error = errorMessage(e) });
  }
  private loadHistory() { if (this.appt) this.ps.history(this.appt.patientId).subscribe({ next: h => this.history = h, error: (e: HttpErrorResponse) => this.error = errorMessage(e) }); }
  private fill(c: Consultation) { this.bp = c.bloodPressure; this.temp = c.temperature; this.notes = c.notes; }
  complete() {
    if (!confirm("Mark this consultation as complete? This cannot be undone.")) return;
    this.saving = true; this.error = "";
    this.cs.save(this.id, { bloodPressure: this.bp, temperature: this.temp!, notes: this.notes }).subscribe({
      next: () => this.cs.complete(this.id).subscribe({ next: () => { this.done = true; this.saving = false; if (this.appt) this.appt.status = "COMPLETED"; this.loadHistory(); }, error: (e: HttpErrorResponse) => { this.error = errorMessage(e); this.saving = false; } }),
      error: (e: HttpErrorResponse) => { this.error = errorMessage(e); this.saving = false; } });
  }
}
