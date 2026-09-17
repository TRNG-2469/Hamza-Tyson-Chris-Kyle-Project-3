import { inject } from '@angular/core';
import { Routes, Router } from '@angular/router';
import { Login } from './login/login';
import { Register } from './register/register';
import { ReimbursementListContainer } from './reimbursement-list-container/reimbursement-list-container'
import { ManagerReimbursementListContainer } from './manager-reimbursement-list-container/manager-reimbursement-list-container';
import { AuthService } from './core/services/auth.service';

const managerGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.currentRole() === 'MANAGER'
    ? true
    : router.parseUrl('/login');
};

export const routes: Routes = [
  // route for login page
  { path: 'login', component: Login },
  // route to register page
  { path: 'register', component: Register },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  // route to dashboard (specifically, my reimbursements page)
  { path: 'reimbursements/:id', component: ReimbursementListContainer },
  {
    path: 'manager/reimbursements',
    component: ManagerReimbursementListContainer,
    canActivate: [managerGuard],
  },
];
