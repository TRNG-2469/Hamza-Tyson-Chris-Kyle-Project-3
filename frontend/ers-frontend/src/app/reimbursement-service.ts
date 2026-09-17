import { inject, Injectable, Service } from '@angular/core';
import { Reimbursement } from './reimbursement';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
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

  getDummyReimbursements(): Reimbursement[] {
    return [
      {
        id: 1,
        amount: 425.75,
        status: 'approved',
        type: 'travel',
        description: 'Round-trip flight for the annual engineering conference.',
        authorId: 2,
        resolverId: 1,
      },
      {
        id: 2,
        amount: 187.4,
        status: 'pending',
        type: 'food',
        description: 'Dinner with the development team after the quarterly planning meeting.',
        authorId: 5,
      },
      {
        id: 3,
        amount: 680.0,
        status: 'approved',
        type: 'lodging',
        description: 'Three-night hotel stay during the client visit.',
        authorId: 4,
        resolverId: 1,
      },
      {
        id: 4,
        amount: 74.99,
        status: 'denied',
        type: 'other',
        description: 'Purchase of notebooks, pens, and other office supplies.',
        authorId: 3,
        resolverId: 7,
      },
    ];
  }

  getReimbursements(): Observable<Reimbursement[]> {
    if (this.userId === null) {
      throw new Error('User ID has not been set.');
    }

    const url = `${this.baseUrl}/reimbursements/${this.userId}`;

    return this.http.get<Reimbursement[]>(url);
  }
}
