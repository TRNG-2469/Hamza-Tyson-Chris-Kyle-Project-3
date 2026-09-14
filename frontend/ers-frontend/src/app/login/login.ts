import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { LoginRequest } from '../core/models/login-request.model';
import { ErrorResponse } from '../core/models/error-response.model';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})

export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly errorMessage = signal<string | null>(null);
  protected readonly submitting = signal(false);

  protected readonly form = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });


  // run when the login form is submitted
  onSubmit(): void {
    // check if form is invalid, marked as touched to show validation errors, and stop method call
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    // clear old error message, try login again
    this.errorMessage.set(null);
    // mark current form as submitting 
    this.submitting.set(true);
 // call the AuthService login method with the form values, and subscribe to the observable returned
    this.authService.login(this.form.getRawValue() as LoginRequest).subscribe({
      next: () => {
        this.submitting.set(false);
        this.router.navigateByUrl('/');
      },
      // retur error if backend end send http erros
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        const body = err.error as ErrorResponse;
        this.errorMessage.set(body?.message ?? 'Login failed. Please try again.');
      }
    });
  }
}