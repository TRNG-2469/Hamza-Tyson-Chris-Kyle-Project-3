import { Component, signal } from '@angular/core';
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
    // singla for login error
  protected readonly errorMessage = signal<string | null>(null);
  // signal for login submitting state
  protected readonly submitting = signal(false);
  //create login form with username and password fields, both required
  protected readonly form = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });
  // constructor injects FormBuilder, AuthService, and Router into the component
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

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