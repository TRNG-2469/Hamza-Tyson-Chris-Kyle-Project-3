export interface Reimbursement {
  id: number;
  amount: number;
  description: string;
  type: 'travel' | 'food' | 'lodging' | 'other';
  status: 'pending' | 'approved' | 'denied';
  authorId: number;
  resolverId?: number;
}
