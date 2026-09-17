import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Reimbursement } from '../reimbursement';
import { AuthService } from '../core/services/auth.service';

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

  get isAuthor(): boolean {
    return this.selectedReimbursement?.authorId === this.authService.userId();
  }

  updateStatus(status: 'approved' | 'denied') {
    if (!this.selectedReimbursement || this.isAuthor) {
      return;
    }

    this.selectedReimbursement.status = status;
    this.statusChanged.emit(this.selectedReimbursement);
  }
}
