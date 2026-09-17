import { Component, inject, Input, signal, WritableSignal } from '@angular/core';
import { Reimbursement } from '../reimbursement';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RegisterRequest } from '../core/models/register-request.model';
import { ReimbursementService } from '../reimbursement-service';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-reimbursement-submission',
  styleUrl: './reimbursement-submission.css',
  templateUrl: './reimbursement-submission.html',
})
export class ReimbursementSubmission {
  private readonly formBuilder: FormBuilder = inject(FormBuilder);
  private readonly reimbursementService: ReimbursementService = inject(ReimbursementService);

  protected readonly errorMessage: WritableSignal<string | null> = signal<string | null>(null);

  protected readonly form = this.formBuilder.nonNullable.group({
    amount: [0, Validators.required],
    description: ['', Validators.required],
    type: ['', Validators.required],
  });

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage.set(null); // Clear any previous error.

    const raw = this.form.getRawValue();
    const request: Reimbursement = {
      id: 0,
      amount: raw.amount,
      description: raw.description,
      type: raw.type,
      status: 'PENDING',
      authorId: 0,
    };
  }
}
