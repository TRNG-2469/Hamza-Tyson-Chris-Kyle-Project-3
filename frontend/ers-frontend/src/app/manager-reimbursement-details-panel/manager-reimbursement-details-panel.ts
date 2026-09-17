import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Reimbursement } from '../reimbursement';
import { AuthService } from '../core/services/auth.service';
import { ReimbursementService } from '../reimbursement-service';

@Component({
  imports: [],
  selector: 'app-manager-reimbursement-details-panel',
  styleUrl: './manager-reimbursement-details-panel.css',
  templateUrl: './manager-reimbursement-details-panel.html',
})
export class ManagerReimbursementDetailsPanel {
  @Input() selectedReimbursement: Reimbursement | null = null;
  @Output() statusChanged = new EventEmitter<Reimbursement>();

  authService = inject(AuthService);
  private readonly reimbursementService = inject(ReimbursementService);

  get isAuthor(): boolean {
    return this.selectedReimbursement?.authorId === this.authService.userId();
  }

  updateStatus(status: 'APPROVED' | 'DENIED') {
    if (!this.selectedReimbursement || this.isAuthor) {
      return;
    }

    this.reimbursementService.updateReimbursementStatus(this.selectedReimbursement, status).subscribe({
      next: (updatedReimbursement) => {
        this.selectedReimbursement!.status = updatedReimbursement?.status ?? status.toLowerCase() as 'approved' | 'denied';
        if (updatedReimbursement?.resolverId !== undefined) {
          this.selectedReimbursement!.resolverId = updatedReimbursement.resolverId;
        }
        this.statusChanged.emit(this.selectedReimbursement!);
      },
      error: (error) => {
        console.error('Unable to update reimbursement status:', error);
      },
    });
  }
}
