import { Service } from '@angular/core';

@Service()
export class ReimbursementService {


    function getDummyReimbursements(): Reimbursement[] { 
        return [ 
            { title: 'Conference Flight', amount: 425.75, date: '2026-09-03', status: 'approved', type: 'travel', description: 'Round-trip flight for the annual engineering conference.', authorName: 'John Smith', resolverName: 'Sarah Johnson' }, 
            { title: 'Team Dinner', amount: 187.40, date: '2026-09-05', status: 'pending', type: 'food', description: 'Dinner with the development team after the quarterly planning meeting.', authorName: 'Emily Davis' }, 
            { title: 'Hotel Stay', amount: 680.00, date: '2026-08-28', status: 'approved', type: 'lodging', description: 'Three-night hotel stay during the client visit.', authorName: 'Michael Wilson', resolverName: 'Sarah Johnson' }, 
            { title: 'Office Supplies', amount: 74.99, date: '2026-09-01', status: 'denied', type: 'other', description: 'Purchase of notebooks, pens, and other office supplies.', authorName: 'John Smith', resolverName: 'David Brown' }, 
            { title: 'Rental Car', amount: 312.50, date: '2026-08-20', status: 'pending', type: 'travel', description: 'Rental car for transportation during the regional sales trip.', authorName: 'Lisa Anderson' } 
        ]; 
    }
}
