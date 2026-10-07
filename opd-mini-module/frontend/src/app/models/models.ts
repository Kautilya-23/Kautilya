export interface Patient { id: number; name: string; gender: string; age: number; phoneNumber: string; }
export interface Doctor { id: number; name: string; specialization: string; }
export interface Appointment { id: number; patientId: number; patientName: string; doctorId: number; doctorName: string; appointmentDateTime: string; status: "BOOKED" | "COMPLETED"; }
export interface Consultation { id: number; appointmentId: number; patientName: string; doctorName: string; bloodPressure: string; temperature: number; notes: string; appointmentDateTime: string; completedAt: string | null; completed: boolean; }
