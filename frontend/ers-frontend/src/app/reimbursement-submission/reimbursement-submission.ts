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
import { RegisterRequest } from '../core/models/register-request.model';
import { ReimbursementService } from '../reimbursement-service';
import { AuthService } from '../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-reimbursement-submission',
  styleUrl: './reimbursement-submission.css',
  templateUrl: './reimbursement-submission.html',
})
export class ReimbursementSubmission {
  @Output()
  isSubmitting:EventEmitter<boolean> = new EventEmitter();

  private readonly formBuilder: FormBuilder = inject(FormBuilder);
  private readonly reimbursementService: ReimbursementService = inject(ReimbursementService);
  private readonly router = inject(Router);

  protected readonly errorMessage: WritableSignal<string | null> = signal<string | null>(null);

  private readonly authService = inject(AuthService);

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
      reimbursementId: 0,
      amount: raw.amount,
      description: raw.description,
      type: raw.type,
      status: 'PENDING',
      authorId: this.authService.userId() ?? -1,
    };
    this.reimbursementService.submitReimbursement(request).subscribe({
      next: (response) => {
        console.log('Reimbursement submitted successfully:', response);
        this.router.navigate(['/reimbursements', this.authService.userId()]); 
      }});
    this.isSubmitting.emit(false);
  }
}
