import { Service } from '@angular/core';
import { Reimbursement } from './reimbursement';

@Service()
export class ReimbursementService {


    getDummyReimbursements(): Reimbursement[] { 
        return [ 
            { id: 1, title: 'Conference Flight', amount: 425.75, date: '2026-09-03', status: 'approved', type: 'travel', description: 'Round-trip flight for the annual engineering conference.', authorName: 'John Smith', resolverName: 'Sarah Johnson' }, 
            { id: 2, title: 'Team Dinner', amount: 187.40, date: '2026-09-05', status: 'pending', type: 'food', description: 'Dinner with the development team after the quarterly planning meeting.', authorName: 'Emily Davis' }, 
            { id: 3, title: 'Hotel Stay', amount: 680.00, date: '2026-08-28', status: 'approved', type: 'lodging', description: 'Three-night hotel stay during the client visit.', authorName: 'Michael Wilson', resolverName: 'Sarah Johnson' }, 
            { id: 4, title: 'Office Supplies', amount: 74.99, date: '2026-09-01', status: 'denied', type: 'other', description: 'Purchase of notebooks, pens, and other office supplies.', authorName: 'John Smith', resolverName: 'David Brown' }
        ]; 
    }
}
