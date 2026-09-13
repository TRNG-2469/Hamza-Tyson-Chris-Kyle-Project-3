export interface Reimbursement { 
    id: number;
    title: string; 
    amount: number; 
    date: string; 
    status: 'pending' | 'approved' | 'denied'; 
    type: 'travel' | 'food' | 'lodging' | 'other'; 
    description: string; 
    authorName: string; 
    resolverName?: string; 
}