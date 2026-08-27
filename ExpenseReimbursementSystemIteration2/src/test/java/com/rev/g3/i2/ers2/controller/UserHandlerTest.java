package com.rev.g3.i2.ers2.controller;

import com.rev.g3.i2.ers2.config.SecurityConfig;
import com.rev.g3.i2.ers2.enums.Role;
import com.rev.g3.i2.ers2.exception.DepartmentNotFoundException;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-slice tests for {@link UserHandler} (register / login / current user) with the real
 * {@link SecurityConfig}. {@link UserService} and {@link JwtService} are mocked; the HTTP status mapping
 * produced by GlobalExceptionHandler for each service outcome is asserted.
 */
@WebMvcTest(UserHandler.class)
@Import({SecurityConfig.class, JwtAuthFilter.class})
class UserHandlerTest {

    private static final String REGISTER_BODY =
            "{\"username\":\"newuser\",\"password\":\"Password1\",\"firstName\":\"New\",\"lastName\":\"User\",\"departmentId\":1}";
    private static final String LOGIN_BODY = "{\"username\":\"newuser\",\"password\":\"Password1\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private static User sampleUser(int id, Role role) {
        User u = new User();
        u.setUserId(id);
        u.setUsername("newuser");
        u.setFirstName("New");
        u.setLastName("User");
        u.setRole(role);
        u.setDepartmentId(1);
        return u;
    }

    // ----------------------------- POST /register -----------------------------

    @Test
    void register_isPublic_returns200_onSuccess() throws Exception {
        when(userService.register(any(User.class))).thenReturn(new User());

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY))
                .andExpect(status().isOk());

        verify(userService).register(any(User.class));
    }

    @Test
    void register_returns409_whenUsernameAlreadyExists() throws Exception {
        when(userService.register(any(User.class)))
                .thenThrow(new UsernameAlreadyExistsException("newuser"));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void register_returns404_whenDepartmentNotFound() throws Exception {
        when(userService.register(any(User.class)))
                .thenThrow(new DepartmentNotFoundException(999));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void register_returns400_onIllegalArgumentFromService() throws Exception {
        when(userService.register(any(User.class)))
                .thenThrow(new IllegalArgumentException("Password cannot be null or blank."));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_returns500_onUnexpectedException() throws Exception {
        when(userService.register(any(User.class)))
                .thenThrow(new RuntimeException("db down"));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void register_malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ not valid json "))
                .andExpect(status().isBadRequest());
    }

    // ----------------------------- POST /login -----------------------------

    @Test
    void login_isPublic_returnsTokenUsernameAndRole() throws Exception {
        when(userService.login("newuser", "Password1")).thenReturn(sampleUser(7, Role.MANAGER));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("signed.jwt.token");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("signed.jwt.token"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("MANAGER"));
    }

    @Test
    void login_badCredentials_returns401() throws Exception {
        when(userService.login("newuser", "Password1")).thenReturn(null);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_BODY))
                .andExpect(status().isUnauthorized());

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_blankPassword_returns400_fromBeanValidation() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newuser\",\"password\":\"   \"}"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).login(any(), any());
    }

    // ----------------------------- GET /user -----------------------------

    @Test
    void currentUser_returnsPrincipalUser_withoutPassword() throws Exception {
        User u = sampleUser(7, Role.EMPLOYEE);
        u.setPassword("bcrypt-hash");

        mockMvc.perform(get("/user").with(user(new UserPrincipal(u))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(7))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("EMPLOYEE"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void currentUser_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isUnauthorized());
    }
}
