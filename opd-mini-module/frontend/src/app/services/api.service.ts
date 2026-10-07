import { Injectable } from "@angular/core";
import { HttpClient, HttpErrorResponse, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { Appointment, Consultation, Doctor, Patient } from "../models/models";

const API = "http://localhost:8080/api";

/** Turns any HttpErrorResponse into a user-friendly message (no raw backend details). */
export function errorMessage(e: HttpErrorResponse): string {
  if (e.status === 0) return "Cannot reach the server. Please check that the backend is running.";
  return e.error?.message ?? "Unexpected error. Please try again.";
}

@Injectable({ providedIn: "root" })
export class PatientService {
  constructor(private http: HttpClient) {}
  list(q = ""): Observable<Patient[]> { return this.http.get<Patient[]>(API + "/patients", { params: new HttpParams().set("q", q) }); }
  create(p: Omit<Patient, "id">): Observable<Patient> { return this.http.post<Patient>(API + "/patients", p); }
  history(id: number): Observable<Consultation[]> { return this.http.get<Consultation[]>(API + "/patients/" + id + "/consultations"); }
}
@Injectable({ providedIn: "root" })
export class AppointmentService {
  constructor(private http: HttpClient) {}
  doctors(): Observable<Doctor[]> { return this.http.get<Doctor[]>(API + "/doctors"); }
  today(): Observable<Appointment[]> { return this.http.get<Appointment[]>(API + "/appointments/today"); }
  get(id: number): Observable<Appointment> { return this.http.get<Appointment>(API + "/appointments/" + id); }
  book(b: { patientId: number; doctorId: number; appointmentDateTime: string }): Observable<Appointment> { return this.http.post<Appointment>(API + "/appointments", b); }
}
@Injectable({ providedIn: "root" })
export class ConsultationService {
  constructor(private http: HttpClient) {}
  get(appId: number): Observable<Consultation> { return this.http.get<Consultation>(API + "/appointments/" + appId + "/consultation"); }
  save(appId: number, c: { bloodPressure: string; temperature: number; notes: string }): Observable<Consultation> { return this.http.post<Consultation>(API + "/appointments/" + appId + "/consultation", c); }
  complete(appId: number): Observable<Consultation> { return this.http.patch<Consultation>(API + "/appointments/" + appId + "/complete", {}); }
}
