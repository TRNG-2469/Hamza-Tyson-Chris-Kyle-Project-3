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
  // testing without dashboard page, show the username of the logged in user
  protected readonly loggedInAs = signal<string | null>(null);

  protected readonly form = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });
  // handle the form submission, validate the form, call the AuthService login method, and handle the response or error
    onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    // reset the error message and set the submitting signal to true
    this.errorMessage.set(null);
    this.submitting.set(true);

    this.authService.login(this.form.getRawValue() as LoginRequest).subscribe({
      next: (response) => {
        this.submitting.set(false);
        // for testing successful login, later will routet to dashboard page
        this.loggedInAs.set(response.username);
      },
      //return an error message if the login fails, and reset the submitting signal
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        const body = err.error as ErrorResponse;
        this.errorMessage.set(body?.message ?? 'Login failed. Please try again.');
      }
    });
  }}