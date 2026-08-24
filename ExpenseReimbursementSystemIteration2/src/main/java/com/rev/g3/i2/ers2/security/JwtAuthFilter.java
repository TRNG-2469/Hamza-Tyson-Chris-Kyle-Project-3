package com.rev.g3.i2.ers2.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
// registers this filter as a Spring bean so it can be picked up in step 9
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        // pull the raw Authorization header off the request
        String authHeader = request.getHeader("Authorization");

        // no token present, nothing to authenticate
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // strip "Bearer " (7 characters) to get the raw token
        String token = authHeader.substring(7);

        try {
            // pull the username out of the token's subject claim
            String username = jwtService.extractUsername(token);

            // only authenticate if we found a username AND nothing is authenticated yet
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // reload the user fresh from the database via CustomUserDetailsService
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // signature, expiration, and username all check out
                if (jwtService.isTokenValid(token, userDetails)) {

                    // Spring Security create an authenticated user object for this userDetails, with no password stored, and with these authorities
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());


                    // attach extra request metadata (IP, session id) — standard practice
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // this is the actual "login" for this request — downstream checks read this
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                }
            }
        } catch (Exception e) {


        }
        // always pass control along, authenticated or not
        filterChain.doFilter(request, response);

    }
}