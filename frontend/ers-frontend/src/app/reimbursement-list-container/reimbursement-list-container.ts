import { Component } from '@angular/core';
import { ReimbursementList } from '../reimbursement-list/reimbursement-list';
import { ReimbursementDetailsPanel } from '../reimbursement-details-panel/reimbursement-details-panel';
import { Reimbursement } from '../reimbursement';

@Component({
  imports: [ReimbursementList, ReimbursementDetailsPanel],
  selector: 'app-reimbursement-list-container',
  styleUrl: './reimbursement-list-container.css',
  templateUrl: './reimbursement-list-container.html',
})
export class ReimbursementListContainer {

    selectedReimbursement: Reimbursement | null = null;

    handleSelectReimbursement(reimbursement: Reimbursement) {
        this.selectedReimbursement = reimbursement;
    }
}
