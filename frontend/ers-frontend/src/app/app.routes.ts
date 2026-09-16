import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Register } from './register/register';
import { ReimbursementListContainer } from './reimbursement-list-container/reimbursement-list-container'

export const routes: Routes = [
  // route for login page
  { path: 'login', component: Login },
  // route to register page
  { path: 'register', component: Register },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  // route to dashboard (specifically, my reimbursements page)
  { path: 'reimbursements/:id', component: ReimbursementListContainer },
];
