import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Reimbursement } from '../reimbursement';

@Component({
  imports: [],
  selector: 'app-reimbursement-details-panel',
  styleUrl: './reimbursement-details-panel.css',
  templateUrl: './reimbursement-details-panel.html',
})
export class ReimbursementDetailsPanel {
  @Input() selectedReimbursement: Reimbursement | null = null;

  @Output()
  editStarted: EventEmitter<boolean> = new EventEmitter<boolean>();

  startEdit() {
    this.editStarted.emit(true);
  }
}
