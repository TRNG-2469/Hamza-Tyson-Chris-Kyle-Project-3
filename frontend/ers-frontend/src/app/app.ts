import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, Router, RouterLink } from '@angular/router';
// import { ReimbursementList } from './reimbursement-list/reimbursement-list';
import { AuthService } from './core/services/auth.service';

@Component({
  imports: [RouterOutlet, RouterLink],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  // angular provide the AuthService object 
  protected readonly authService = inject(AuthService);
  // angular give Router to navigate between pages
  private readonly router = inject(Router); 
  protected readonly title = signal('ers-frontend');

  // click logout button
  onLogout(): void{
    // call the inject service logout method
    this.authService.logout();
    // send user back to login page
    this.router.navigateByUrl('/login')
  }
  
}
