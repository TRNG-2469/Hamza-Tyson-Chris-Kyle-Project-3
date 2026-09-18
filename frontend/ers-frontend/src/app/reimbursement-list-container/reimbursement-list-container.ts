import { Component, inject, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ReimbursementList } from '../reimbursement-list/reimbursement-list';
import { ReimbursementDetailsPanel } from '../reimbursement-details-panel/reimbursement-details-panel';
import { Reimbursement } from '../reimbursement';
import { ReimbursementEdit } from '../reimbursement-edit/reimbursement-edit';
import { AuthService } from '../core/services/auth.service';
import { Router } from '@angular/router';
import { ReimbursementSubmission } from '../reimbursement-submission/reimbursement-submission';


@Component({
  imports: [
    ReimbursementList,
    ReimbursementDetailsPanel,
    ReimbursementEdit,
    RouterLink,
    ReimbursementSubmission,
  ],
  selector: 'app-reimbursement-list-container',
  styleUrl: './reimbursement-list-container.css',
  templateUrl: './reimbursement-list-container.html',
})
export class ReimbursementListContainer {
  authService: AuthService = inject(AuthService);
  private readonly router = inject(Router);
  @ViewChild(ReimbursementList) reimbursementList?: ReimbursementList;

  selectedReimbursement: Reimbursement | null = null;
  isEditing: boolean = false;
  isSubmitting: boolean = false;

  checkEdit(data: boolean) {
    this.isEditing = data;
    if (!data) {
      this.reimbursementList?.refresh();
    }
  }

  checkSubmit(data: boolean) {
    this.isSubmitting = data;
    if(!data){
      this.reimbursementList?.refresh();
    }
  }

  startSubmit() {
    this.isEditing = false;
    this.isSubmitting = true;
  }

  endSubmit() {
    this.isSubmitting = false;
  }

  handleSelectReimbursement(reimbursement: Reimbursement) {
    this.selectedReimbursement = reimbursement;
  }

  // logout method
  onLogout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}
