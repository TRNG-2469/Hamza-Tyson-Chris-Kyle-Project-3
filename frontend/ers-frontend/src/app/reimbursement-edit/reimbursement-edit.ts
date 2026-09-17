import {
  Component,
  EventEmitter,
  inject,
  Input,
  Output,
  signal,
  WritableSignal,
} from '@angular/core';
import { Reimbursement } from '../reimbursement';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReimbursementService } from '../reimbursement-service';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-reimbursement-edit',
  styleUrl: './reimbursement-edit.css',
  templateUrl: './reimbursement-edit.html',
  standalone: true,
})
export class ReimbursementEdit {
  private readonly formBuilder: FormBuilder = inject(FormBuilder);
  private readonly reimbursementService: ReimbursementService = inject(ReimbursementService);

  protected readonly errorMessage: WritableSignal<string | null> = signal<string | null>(null);

  @Input() selectedReimbursement: Reimbursement | null = null;

  @Output()
  isEditing: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output()
  reimbursementChanged: EventEmitter<Reimbursement> = new EventEmitter<Reimbursement>();

  protected readonly form = this.formBuilder.nonNullable.group({
    amount: [0, Validators.required],
    description: ['', Validators.required],
    type: ['', Validators.required],
  });

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage.set(null); // Clear any previous error.

    const raw = this.form.getRawValue();
    const request: Reimbursement = {
      reimbursementId: this.selectedReimbursement!.reimbursementId,
      amount: raw.amount,
      description: raw.description,
      type: raw.type,
      status: 'PENDING',
      authorId: 0,
    };

    this.reimbursementService.updateReimbursement(request).subscribe({
      next: (response) => {
        console.log('Reimbursement update successfully:', response);

        this.isEditing.emit(false);
        this.reimbursementChanged.emit(this.selectedReimbursement!);
      }
    });
  }

  cancelEdits() {
    this.isEditing.emit(false);
  }
}
