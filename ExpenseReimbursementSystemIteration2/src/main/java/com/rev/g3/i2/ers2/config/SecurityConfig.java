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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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

    // the Angular dev server runs on its own origin (localhost:4200), separate from the backend (localhost:8080)
    // browsers block cross-origin requests by default, so we need to explicitly allow the frontend's origin here
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // only the Angular dev server is allowed to call this API for now
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        // every HTTP method our controllers use, plus OPTIONS for the browser's preflight check
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Content-Type for JSON bodies, Authorization so our JWT bearer token is allowed through
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization"));

        // apply this CORS policy to every endpoint in the app
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                // wire the CORS policy above into Spring Security's filter chain — without this,
                // Security's own filters would block the browser's preflight OPTIONS request
                // before it ever reaches the CORS headers above
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF protects cookie-based sessions; irrelevant for stateless bearer-token auth
                .csrf(AbstractHttpConfigurer::disable)

                // never create or read an HttpSession — every request re-authenticates via its token
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //  regsiter and lgoin are reachable with no token at all
                .authorizeHttpRequests(auth -> auth
                        // register and login are reachable with no token needed
                        .requestMatchers("/register", "/login").permitAll()
                        .requestMatchers("/departments", "/departments/*").permitAll()
                        // map and allow swagger ui to be be open to public, anyone can access it
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()


                        // let these html come through pleaseeee
                                .requestMatchers("/", "/*.html", "/*.css").permitAll()
                        // approve/deny and the all-reimbursements view are manager-only

                        .requestMatchers("/manager/**").hasRole("MANAGER")



                        .anyRequest().authenticated()
                )
                // our JWT check before Spring's default filter, so it can populate the SecurityContext first
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}