import { Component, Inject } from '@angular/core';
import { ReimbursementList } from '../reimbursement-list/reimbursement-list';
import { ReimbursementDetailsPanel } from '../reimbursement-details-panel/reimbursement-details-panel';
import { Reimbursement } from '../reimbursement';
import { ReimbursementEdit } from '../reimbursement-edit/reimbursement-edit';
import { AuthService } from '../core/services/auth.service';

@Component({
  imports: [ReimbursementList, ReimbursementDetailsPanel, ReimbursementEdit],
  selector: 'app-reimbursement-list-container',
  styleUrl: './reimbursement-list-container.css',
  templateUrl: './reimbursement-list-container.html',
})
export class ReimbursementListContainer {
    authService: AuthService = Inject(AuthService);

    selectedReimbursement: Reimbursement | null = null;
    startedEdit: boolean = false;

    handleSelectReimbursement(reimbursement: Reimbursement) {
        this.selectedReimbursement = reimbursement;
    }
}
