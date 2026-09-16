import { Component, Input } from '@angular/core';
import { Reimbursement } from '../reimbursement';

@Component({
  imports: [],
  selector: 'app-reimbursement-details-panel',
  styleUrl: './reimbursement-details-panel.css',
  templateUrl: './reimbursement-details-panel.html',
})
export class ReimbursementDetailsPanel {

    @Input() selectedReimbursement: Reimbursement | null = null;
}
