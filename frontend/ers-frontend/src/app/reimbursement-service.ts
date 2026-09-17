import { inject, Injectable, Service } from '@angular/core';
import { Reimbursement } from './reimbursement';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';


@Service()
export class ReimbursementService {
  private userId: number | null = null;
  private baseUrl = 'http://localhost:8080';

  setUserId(userId: number): void {
    this.userId = userId;
  }

  getUserId(): number | null {
    return this.userId;
  }

  private http = inject(HttpClient);



  getReimbursements(): Observable<Reimbursement[]> {
    if (this.userId === null) {
      throw new Error('User ID has not been set.');
    }

    const url = `${this.baseUrl}/reimbursements/${this.userId}`;

    return this.http.get<Reimbursement[]>(url);
  }
  getAllReimbursements(): Observable<Reimbursement[]> {
    const url = `${this.baseUrl}/manager/reimbursements`;

    return this.http.get<Reimbursement[]>(url);
  }
  updateReimbursement(reimbursement: Reimbursement): Observable<Reimbursement> {
    const url = `${this.baseUrl}/reimbursements/${reimbursement.reimbursementId}`;

    return this.http.patch<Reimbursement>(url, reimbursement);
  }

  updateReimbursementStatus(
    reimbursement: Reimbursement,
    status: 'APPROVED' | 'DENIED',
  ): Observable<Reimbursement | null> {
    const url = `${this.baseUrl}/manager/reimbursements/${reimbursement.reimbursementId}`;

    return this.http.patch<Reimbursement>(url, { ...reimbursement, status });
  }

  submitReimbursement(reimbursement: Reimbursement): Observable<Reimbursement> {
    const url = `${this.baseUrl}/reimbursements`;

    return this.http.post<Reimbursement>(url, reimbursement);
  }
}
