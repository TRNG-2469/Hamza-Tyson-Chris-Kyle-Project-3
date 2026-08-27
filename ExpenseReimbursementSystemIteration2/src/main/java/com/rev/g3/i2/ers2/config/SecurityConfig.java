package com.rev.g3.i2.ers2.config;

import com.rev.g3.i2.ers2.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                // CSRF protects cookie-based sessions; irrelevant for stateless bearer-token auth
                .csrf(AbstractHttpConfigurer::disable)
                // never create or read an HttpSession — every request re-authenticates via its token
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // unauthenticated -> plain 401 (no login-page redirect)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        // static front-end pages
                        .requestMatchers("/", "/index.html", "/login.html", "/register.html", "/dashboard.html",
                                "/css/**", "/js/**", "/favicon.ico").permitAll()
                        // register / login reachable with no token (both the plain and /api paths)
                        .requestMatchers("/register", "/login", "/api/register", "/api/login").permitAll()
                        // the registration page needs the department list before the user has an account
                        .requestMatchers(HttpMethod.GET, "/departments", "/departments/**").permitAll()
                        // manager-only endpoints
                        .requestMatchers("/manager/**").hasRole("MANAGER")
                        // everything else requires a valid token
                        .anyRequest().authenticated()
                )
                // our JWT check before Spring's default filter, so it can populate the SecurityContext first
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
