import { Component, inject, ViewChild } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { Reimbursement } from '../reimbursement';
import { ManagerReimbursementDetailsPanel } from '../manager-reimbursement-details-panel/manager-reimbursement-details-panel';
import { ManagerReimbursementList } from '../manager-reimbursement-list/manager-reimbursement-list';
import { ReimbursementSubmission } from '../reimbursement-submission/reimbursement-submission';

@Component({
  imports: [
    ManagerReimbursementList,
    ManagerReimbursementDetailsPanel,
    RouterLink,
    ReimbursementSubmission,
  ],
  selector: 'app-manager-reimbursement-list-container',
  styleUrl: './manager-reimbursement-list-container.css',
  templateUrl: './manager-reimbursement-list-container.html',
})
export class ManagerReimbursementListContainer {
  readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  @ViewChild(ManagerReimbursementList) reimbursementList?: ManagerReimbursementList;
  selectedReimbursement: Reimbursement | null = null;
  isSubmitting = false;

  startSubmit() {
    this.isSubmitting = true;
  }

  handleSelectReimbursement(reimbursement: Reimbursement) {
    this.selectedReimbursement = reimbursement;
  }

  handleStatusChanged(reimbursement: Reimbursement) {
    this.selectedReimbursement = reimbursement;
    this.reimbursementList?.refresh();
  }

  onLogout() {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}
