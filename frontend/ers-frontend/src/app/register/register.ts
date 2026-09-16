import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { DepartmentService } from '../core/services/department.service';
import { Department } from '../core/models/department.model';
import { RegisterRequest } from '../core/models/register-request.model';
import { ErrorResponse } from '../core/models/error-response.model';

// Allow only English letters, with at least one letter; no spaces or numbers.
const NAME_PATTERN = /^[a-zA-Z]+$/;

// Tell Angular which HTML, CSS, and form features this component uses.
@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  // Ask Angular to supply these dependencies.
  private readonly fb = inject(FormBuilder); // Builds the registration form.
  private readonly authService = inject(AuthService); // Sends registration requests.
  private readonly departmentService = inject(DepartmentService); // Loads departments.
  private readonly router = inject(Router); // Navigates between pages.

  // Signals hold values that Angular can track and display in the template.
  // readonly should prevents replacing the signal, .set() can still change its value.
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly submitting = signal(false);
  protected readonly departments = signal<Department[]>([]);

  // Create form fields with starting values and validation rules.
  protected readonly form = this.fb.nonNullable.group({
    username: ['', Validators.required], // Cannot be empty.
    password: ['', Validators.required],
    firstName: ['', [Validators.required, Validators.pattern(NAME_PATTERN)]],
    lastName: ['', [Validators.required, Validators.pattern(NAME_PATTERN)]],
    departmentId: ['', Validators.required] // User must choose a department.
  });

  constructor() {
    // getAll() returns an Observable; subscribe() starts the HTTP request.
    this.departmentService.getAll().subscribe({
      // On success, save the returned departments in the signal.
      next: (departments) => this.departments.set(departments),

      // On failure, store an error message for the template to display.
      error: () => this.errorMessage.set('Could not load departments. Please try again later.')
    });
  }

  // Called when the user submits the form; returns no value.
  onSubmit(): void {
    if (this.form.invalid) {
      // Mark every field as touched so the template can show validation errors.
      this.form.markAllAsTouched();
      return; // Stop here if validation fails.
    }

    this.errorMessage.set(null); // Clear any previous error.
    this.submitting.set(true); // Let the template show loading or disable Submit.

    // Read all form values, including values of disabled fields.
    const raw = this.form.getRawValue();

    // the object that will be sent in the HTTP request body
    const request: RegisterRequest = {
      username: raw.username,
      password: raw.password,
      firstName: raw.firstName,
      lastName: raw.lastName,
      departmentId: Number(raw.departmentId) // Convert the selected ID to a number.
    };

    // register() returns an Observable; subscribe() sends the registration request.
    this.authService.register(request).subscribe({
      // Runs when the backend responds successfully; no response data is needed.
      next: () => {
        this.submitting.set(false);
        this.router.navigateByUrl('/login'); // Send the user to the login page.
      },

      // Runs if the HTTP request fails.
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);

        // Treat the backend's error body as ErrorResponse.
        // "as" tells TypeScript the expected type; it does not validate the data.
        const body = err.error as ErrorResponse;

        // Use the backend message, or a fallback if it is null or undefined.
        this.errorMessage.set(body?.message ?? 'Registration failed. Please try again.');
      }
    });
  }
}