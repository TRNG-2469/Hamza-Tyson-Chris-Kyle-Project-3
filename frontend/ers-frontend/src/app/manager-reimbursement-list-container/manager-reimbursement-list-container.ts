import { Component, inject } from '@angular/core';
import { AuthService } from '../core/services/auth.service';
import { Reimbursement } from '../reimbursement';
import { ManagerReimbursementDetailsPanel } from '../manager-reimbursement-details-panel/manager-reimbursement-details-panel';
import { ManagerReimbursementList } from '../manager-reimbursement-list/manager-reimbursement-list';

@Component({
  imports: [ManagerReimbursementList, ManagerReimbursementDetailsPanel],
  selector: 'app-manager-reimbursement-list-container',
  styleUrl: './manager-reimbursement-list-container.css',
  templateUrl: './manager-reimbursement-list-container.html',
})
export class ManagerReimbursementListContainer {
  readonly authService = inject(AuthService);
  selectedReimbursement: Reimbursement | null = null;

  handleSelectReimbursement(reimbursement: Reimbursement) {
    this.selectedReimbursement = reimbursement;
  }
}
