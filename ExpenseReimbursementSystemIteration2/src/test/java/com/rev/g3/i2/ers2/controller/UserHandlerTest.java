package com.rev.g3.i2.ers2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rev.g3.i2.ers2.config.SecurityConfig;
import com.rev.g3.i2.ers2.enums.Role;
import com.rev.g3.i2.ers2.exception.DepartmentNotFoundException;
import com.rev.g3.i2.ers2.exception.GlobalExceptionHandler;
import com.rev.g3.i2.ers2.exception.UsernameAlreadyExistsException;
import com.rev.g3.i2.ers2.model.User;
import com.rev.g3.i2.ers2.security.JwtAuthFilter;
import com.rev.g3.i2.ers2.security.JwtService;
import com.rev.g3.i2.ers2.security.UserPrincipal;
import com.rev.g3.i2.ers2.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-slice tests for {@link UserHandler}: registration status mapping via {@link GlobalExceptionHandler},
 * login token issuance via a mocked {@link AuthenticationManager}, and the /user "who am I" endpoint.
 */
@WebMvcTest(UserHandler.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, GlobalExceptionHandler.class})
class UserHandlerTest {

    @Autowired private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean private UserService userService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private AuthenticationManager authenticationManager; // replaces SecurityConfig's bean

    private String registerBody() throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "username", "newuser", "password", "Password1",
                "firstName", "New", "lastName", "User", "departmentId", 1));
    }

    private static User userWithRole(int id, Role role) {
        User u = new User();
        u.setUserId(id);
        u.setUsername("alice");
        u.setPassword("$2a$10$hash");
        u.setRole(role);
        return u;
    }

    // ----------------------------- POST /register -----------------------------

    @Test
    void register_isPublic_andReturns200() throws Exception {
        when(userService.register(any(User.class))).thenReturn(new User());

        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(registerBody()))
                .andExpect(status().isOk());

        verify(userService).register(argThat(u -> "newuser".equals(u.getUsername()) && u.getDepartmentId() == 1));
    }

    @Test
    void register_duplicateUsername_returns409() throws Exception {
        when(userService.register(any())).thenThrow(new UsernameAlreadyExistsException("newuser"));

        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(registerBody()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.path").value("/register"));
    }

    @Test
    void register_unknownDepartment_returns404() throws Exception {
        when(userService.register(any())).thenThrow(new DepartmentNotFoundException(999));

        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(registerBody()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Department not found with ID: 999"));
    }

    @Test
    void register_serviceValidation_returns400() throws Exception {
        when(userService.register(any())).thenThrow(new IllegalArgumentException("Password cannot be null or blank."));

        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(registerBody()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_unexpectedError_returns500_withGenericMessage() throws Exception {
        when(userService.register(any())).thenThrow(new RuntimeException("db down"));

        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(registerBody()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please try again later."));
    }

    // ----------------------------- POST /login -----------------------------

    @Test
    void login_success_returnsTokenUsernameRole() throws Exception {
        UserPrincipal principal = new UserPrincipal(userWithRole(1, Role.MANAGER));
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        when(jwtService.generateToken(principal)).thenReturn("jwt-123");

        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "alice", "password", "pw"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-123"))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.role").value("MANAGER"));

        verify(authenticationManager).authenticate(argThat(a ->
                "alice".equals(a.getPrincipal()) && "pw".equals(a.getCredentials())));
    }

    @Test
    void login_badCredentials_returns401() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "alice", "password", "wrong"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password."));

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_blankFields_returns400_beforeAuthenticating() throws Exception {
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "", "password", ""))))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    // ----------------------------- GET /user -----------------------------

    @Test
    void currentUser_returnsDto_withoutPassword() throws Exception {

        mockMvc.perform(get("/user").with(user(new UserPrincipal(userWithRole(7, Role.EMPLOYEE)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(7))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.role").value("EMPLOYEE"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void currentUser_unauthenticated_isRejected() throws Exception {
        mockMvc.perform(get("/user")).andExpect(status().is4xxClientError());
    }
}
