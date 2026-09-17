import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Reimbursement } from '../reimbursement';

@Component({
  imports: [],
  selector: 'app-manager-reimbursement-details-panel',
  styleUrl: './manager-reimbursement-details-panel.css',
  templateUrl: './manager-reimbursement-details-panel.html',
})
export class ManagerReimbursementDetailsPanel {
  @Input() selectedReimbursement: Reimbursement | null = null;
  @Input() currentUsername: string | null = null;
  @Output() statusChanged = new EventEmitter<Reimbursement>();

  get isAuthor(): boolean {
    return this.selectedReimbursement?.authorUsername === this.currentUsername;
  }

  updateStatus(status: 'approved' | 'denied') {
    if (!this.selectedReimbursement || this.isAuthor) {
      return;
    }

    this.selectedReimbursement.status = status;
    this.statusChanged.emit(this.selectedReimbursement);
  }
}
