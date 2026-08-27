package com.rev.g3.i2.ers2.controller;

import com.rev.g3.i2.ers2.config.SecurityConfig;
import com.rev.g3.i2.ers2.model.Department;
import com.rev.g3.i2.ers2.security.JwtAuthFilter;
import com.rev.g3.i2.ers2.security.JwtService;
import com.rev.g3.i2.ers2.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-slice tests for {@link DepartmentHandler}. Loads only the MVC layer for this controller plus the
 * real {@link SecurityConfig} and {@link JwtAuthFilter}; {@link DepartmentService}, {@link JwtService}
 * and {@link UserDetailsService} are mocked (the filter simply passes requests through when there is
 * no Authorization header).
 */
@WebMvcTest(DepartmentHandler.class)
@Import({SecurityConfig.class, JwtAuthFilter.class})
class DepartmentHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void getDepartmentById_returns200_andJsonBody() throws Exception {
        when(departmentService.findByDepartmentId(1))
                .thenReturn(new Department(1, "Engineering"));

        mockMvc.perform(get("/departments/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentId").value(1))
                .andExpect(jsonPath("$.departmentName").value("Engineering"));
    }

    @Test
    void getDepartmentById_bindsPathVariable() throws Exception {
        when(departmentService.findByDepartmentId(7))
                .thenReturn(new Department(7, "Finance"));

        mockMvc.perform(get("/departments/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentName").value("Finance"));

        verify(departmentService).findByDepartmentId(7);
    }

    @Test
    void getDepartmentById_nonNumericId_returns400() throws Exception {
        // "abc" cannot bind to int -> MethodArgumentTypeMismatch -> 400 via GlobalExceptionHandler
        mockMvc.perform(get("/departments/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDepartmentById_serviceThrowsIllegalArgument_returns400() throws Exception {
        when(departmentService.findByDepartmentId(anyInt()))
                .thenThrow(new IllegalArgumentException("Department ID cannot be negative or zero."));

        mockMvc.perform(get("/departments/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllDepartments_returns200_andJsonArray() throws Exception {
        when(departmentService.queryDepartments())
                .thenReturn(List.of(new Department(1, "Engineering"), new Department(2, "Finance")));

        mockMvc.perform(get("/departments").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].departmentName").value("Engineering"))
                .andExpect(jsonPath("$[1].departmentId").value(2));
    }

    @Test
    void getAllDepartments_returnsEmptyArray_whenNone() throws Exception {
        when(departmentService.queryDepartments()).thenReturn(List.of());

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getDepartments_withoutAuthentication_isAllowed() throws Exception {
        // The registration page lists departments before the user has an account,
        // so SecurityConfig permits anonymous GET /departments.
        when(departmentService.queryDepartments()).thenReturn(List.of(new Department(1, "Engineering")));

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void otherEndpoints_withoutAuthentication_return401() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isUnauthorized());
    }
}
