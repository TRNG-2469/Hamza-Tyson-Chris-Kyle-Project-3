package com.rev.g3.i2.ers2.config;

import com.rev.g3.i2.ers2.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
// turns on Spring Security's web support explicitly, rather than relying on autoconfig defaults
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // hands back the manager Spring Security already built from our CustomUserDetailsService + PasswordEncoder beans — AuthController will use this to check credentials
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                // CSRF protects cookie-based sessions; irrelevant for stateless bearer-token auth
                .csrf(AbstractHttpConfigurer::disable)

                // never create or read an HttpSession — every request re-authenticates via its token
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //  regsiter and lgoin are reachable with no token at all
                .authorizeHttpRequests(auth -> auth
                        // register and login are reachable with no token needed
                        .requestMatchers("/", "/index.html", "/register.html", "/dashboard.html").permitAll()
                        .requestMatchers("/register", "/login").permitAll()
                        .requestMatchers("/departments", "/departments/*").permitAll()
                        .requestMatchers("/manager/**").hasRole("MANAGER")
                        .anyRequest().authenticated()
                )
                // our JWT check before Spring's default filter, so it can populate the SecurityContext first
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}