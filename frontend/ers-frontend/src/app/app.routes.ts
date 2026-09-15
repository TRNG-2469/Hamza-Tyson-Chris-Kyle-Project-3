import { Routes } from '@angular/router';
import { Login } from './login/login';

export const routes: Routes = [
    // route for login page
    { path: 'login', component: Login },
    { path: '', redirectTo: 'login', pathMatch: 'full' }
];
