// an interceptor intercept HTTP requests before hitting the backend, check whether the user has jwt token, if yes attach it as Authorization: Bearer <token>

import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

// create and export an interceptor
export const authInterceptor: HttpInterceptorFn = (req, next) => {
    // inject the AuthService into this interceptor 
  const authService = inject(AuthService);
  // get current jwt token from the AuthService token signal
  const token = authService.token();

  // check if no token, for reaching /login and /register before having jwt 
  if (!token) {
    // send the request to backend without Authorization header
    return next(req);
  }

// create a new copy of the origina request 
  const authorizedRequest = req.clone({
    // add the Authorization header with the jwt token to the request
    setHeaders: { Authorization: `Bearer ${token}` }
  });
// send the aut request to backend 
  return next(authorizedRequest);
};