export interface Reimbursement {
  id: number;
  amount: number;
  description: string;
  type: string;
  status: string;
  authorId: number;
  resolverId?: number;
}
