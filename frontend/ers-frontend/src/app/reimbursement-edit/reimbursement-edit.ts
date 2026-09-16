import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Reimbursement } from '../reimbursement';

@Component({
  imports: [],
  selector: 'app-reimbursement-edit',
  styleUrl: './reimbursement-edit.css',
  templateUrl: './reimbursement-edit.html',
  standalone: true,
})
export class ReimbursementEdit {
  @Input() selectedReimbursement: Reimbursement | null = null;

  @Output()
  editStarted: EventEmitter<boolean> = new EventEmitter<boolean>();

  submitEdits() {
    this.editStarted.emit(false);
  }

  cancelEdits() {
    this.editStarted.emit(false);
  }

  @Output() public amount = new EventEmitter();
  @Output() public desc = new EventEmitter();
  @Output() public type = new EventEmitter();
}
