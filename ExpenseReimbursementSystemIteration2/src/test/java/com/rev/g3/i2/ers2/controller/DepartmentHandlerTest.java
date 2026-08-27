package com.rev.g3.i2.ers2.controller;

import com.rev.g3.i2.ers2.config.SecurityConfig;
import com.rev.g3.i2.ers2.exception.GlobalExceptionHandler;
import com.rev.g3.i2.ers2.model.Department;
import com.rev.g3.i2.ers2.security.JwtAuthFilter;
import com.rev.g3.i2.ers2.security.JwtService;
import com.rev.g3.i2.ers2.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-slice tests for {@link DepartmentHandler} with the REAL {@link SecurityConfig} + {@link JwtAuthFilter}
 * (JwtService and UserDetailsService are mocked, so no token parsing or DB access happens).
 * Verifies URL mapping, path binding, JSON output, and that /departments is public.
 */
@WebMvcTest(DepartmentHandler.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, GlobalExceptionHandler.class})
class DepartmentHandlerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private DepartmentService departmentService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private UserDetailsService userDetailsService;

    @Test
    void getById_returnsJson() throws Exception {
        when(departmentService.findByDepartmentId(1)).thenReturn(new Department(1, "Engineering"));

        mockMvc.perform(get("/departments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentId").value(1))
                .andExpect(jsonPath("$.departmentName").value("Engineering"));
    }

    @Test
    void getById_bindsPathVariable() throws Exception {
        when(departmentService.findByDepartmentId(7)).thenReturn(new Department(7, "Finance"));

        mockMvc.perform(get("/departments/7")).andExpect(status().isOk());

        verify(departmentService).findByDepartmentId(7);
    }

    @Test
    void getById_nonNumeric_returns400() throws Exception {
        mockMvc.perform(get("/departments/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void getById_serviceRejectsId_returns400() throws Exception {
        when(departmentService.findByDepartmentId(anyInt()))
                .thenThrow(new IllegalArgumentException("Department ID cannot be negative or zero."));

        mockMvc.perform(get("/departments/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Department ID cannot be negative or zero."));
    }

    @Test
    void getAll_returnsArray() throws Exception {
        when(departmentService.queryDepartments())
                .thenReturn(List.of(new Department(1, "Engineering"), new Department(2, "Finance")));

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].departmentName").value("Finance"));
    }

    @Test
    void getAll_isPublic_noTokenRequired() throws Exception {
        // register page needs this list before the user has logged in
        when(departmentService.queryDepartments()).thenReturn(List.of());

        mockMvc.perform(get("/departments")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getAll_alsoWorksAuthenticated() throws Exception {
        when(departmentService.queryDepartments()).thenReturn(List.of());

        mockMvc.perform(get("/departments")).andExpect(status().isOk());
    }
}
