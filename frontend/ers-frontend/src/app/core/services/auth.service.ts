import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';
import { LoginRequest } from '../models/login-request.model';
import { AuthResponse } from '../models/auth-response.model';
import { RegisterRequest } from '../models/register-request.model';

// stored the login info in browser aka local storage 
const STORAGE_KEY = 'ers_auth';

// a singleton AuthService, can be injected into any component or service in the app
@Injectable({ providedIn: 'root' })
export class AuthService {
    // hold the current login state in a signal 
  private readonly authState = signal<AuthResponse | null>(this.readFromStorage());

  // derived signals that depend on authState, and will update automatically when authState changes
  readonly isAuthenticated = computed(() => this.authState() !== null);
  readonly currentRole = computed(() => this.authState()?.role ?? null);
  readonly token = computed(() => this.authState()?.token ?? null);
  // expose the username
  readonly username = computed(() => this.authState()?.username ?? null);
  readonly userId = computed(() => this.authState()?.userId ?? null);


  constructor(private http: HttpClient) {}
 // send a POST request to backend /login endpoint with the login credentials, and return an observable of AuthResponse
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_BASE_URL}/login`, credentials).pipe(
      tap((response) => {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(response));
        this.authState.set(response);
      })
    );
  }

  
  // send a POST request to backend /register endpoint with the registration data 
  register(request: RegisterRequest): Observable<void> {
    //return an void on successful registration 
      return this.http.post<void>(`${API_BASE_URL}/register`, request);
  }
  
// clear the login state from local storage and reset the authState signal
  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.authState.set(null);
  }
  // read the login state from local storage and return it as an AuthResponse object, or null if not found
  private readFromStorage(): AuthResponse | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  }
}