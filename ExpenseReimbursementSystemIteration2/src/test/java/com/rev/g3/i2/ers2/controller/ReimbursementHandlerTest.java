package com.rev.g3.i2.ers2.controller;

import com.rev.g3.i2.ers2.config.SecurityConfig;
import com.rev.g3.i2.ers2.enums.Role;
import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.enums.Type;
import com.rev.g3.i2.ers2.model.Reimbursement;
import com.rev.g3.i2.ers2.model.User;
import com.rev.g3.i2.ers2.security.JwtAuthFilter;
import com.rev.g3.i2.ers2.security.JwtService;
import com.rev.g3.i2.ers2.security.UserPrincipal;
import com.rev.g3.i2.ers2.service.ReimbursementService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-slice tests for {@link ReimbursementHandler} with the real {@link SecurityConfig}.
 * {@link ReimbursementService} is mocked.
 *
 * Endpoints that read {@code @AuthenticationPrincipal UserPrincipal} are exercised with a real
 * {@link UserPrincipal} via {@code .with(user(principal))}; {@code @WithMockUser} is not used because the Boot 4 web slice does not
 * wire the Spring Security test configurer, and it would inject a Spring {@code User} anyway.
 */
@WebMvcTest(ReimbursementHandler.class)
@Import({SecurityConfig.class, JwtAuthFilter.class})
class ReimbursementHandlerTest {

    private static final String BODY =
            "{\"amount\":250.0,\"description\":\"Client dinner\",\"type\":\"FOOD\",\"status\":\"PENDING\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReimbursementService reimbursementService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private static UserPrincipal principal(int userId, Role role) {
        User u = new User();
        u.setUserId(userId);
        u.setUsername("user" + userId);
        u.setRole(role);
        return new UserPrincipal(u);
    }

    private static UserPrincipal employee() { return employee(); }
    private static UserPrincipal manager()  { return manager(); }

    // ----------------------------- POST /reimbursements (create) -----------------------------

    @Test
    void createReimbursement_returns200_andPassesAuthenticatedAuthor() throws Exception {
        when(reimbursementService.createReimbursement(any(Reimbursement.class), any(User.class)))
                .thenReturn(new Reimbursement());

        mockMvc.perform(post("/reimbursements").with(user(employee()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isOk());

        // The author handed to the service must be the principal's wrapped user (id 5).
        verify(reimbursementService).createReimbursement(any(Reimbursement.class),
                argThat(u -> u.getUserId() == 5));
    }

    @Test
    void createReimbursement_serviceValidationError_returns400() throws Exception {
        when(reimbursementService.createReimbursement(any(Reimbursement.class), any(User.class)))
                .thenThrow(new IllegalArgumentException("Reimbursement amount cannot be negative or zero."));

        mockMvc.perform(post("/reimbursements").with(user(employee()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReimbursement_beanValidationFails_returns400() throws Exception {
        String negativeAmount = "{\"amount\":-5,\"description\":\"x\",\"type\":\"FOOD\"}";

        mockMvc.perform(post("/reimbursements").with(user(employee()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(negativeAmount))
                .andExpect(status().isBadRequest());

        verify(reimbursementService, never()).createReimbursement(any(), any());
    }

    @Test
    void createReimbursement_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(post("/reimbursements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isUnauthorized());
    }

    // ----------------------------- GET /reimbursements/{id} (by author) -----------------------------

    @Test
    void queryByAuthorId_returns200_andList_noStatusFilter() throws Exception {
        Reimbursement r = new Reimbursement();
        r.setReimbursementId(1);
        r.setAmount(100.0);
        r.setDescription("Taxi");
        r.setType(Type.TRAVEL);
        r.setStatus(Status.PENDING);
        when(reimbursementService.queryReimbursementsByAuthorId(5, null)).thenReturn(List.of(r));

        mockMvc.perform(get("/reimbursements/5").with(user(employee())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Taxi"));
    }

    @Test
    void queryByAuthorId_passesParsedStatusFilter() throws Exception {
        when(reimbursementService.queryReimbursementsByAuthorId(eq(5), eq(Status.APPROVED)))
                .thenReturn(List.of());

        mockMvc.perform(get("/reimbursements/5").with(user(employee())).param("status", "APPROVED"))
                .andExpect(status().isOk());

        verify(reimbursementService).queryReimbursementsByAuthorId(5, Status.APPROVED);
    }

    @Test
    void queryByAuthorId_invalidStatus_returns400_withoutCallingService() throws Exception {
        mockMvc.perform(get("/reimbursements/5").with(user(employee())).param("status", "NOPE"))
                .andExpect(status().isBadRequest());

        verify(reimbursementService, never()).queryReimbursementsByAuthorId(anyInt(), any());
    }

    @Test
    void queryByAuthorId_nonNumericId_returns400() throws Exception {
        mockMvc.perform(get("/reimbursements/abc").with(user(employee())))
                .andExpect(status().isBadRequest());
    }

    // ----------------------------- GET /manager/reimbursements (manager view) -----------------------------

    @Test
    void managerQuery_returns200_withStatusAndDepartmentFilters() throws Exception {
        when(reimbursementService.queryReimbursements(eq(Status.PENDING), eq(3)))
                .thenReturn(List.of());

        mockMvc.perform(get("/manager/reimbursements").with(user(manager()))
                        .param("status", "PENDING")
                        .param("departmentId", "3"))
                .andExpect(status().isOk());

        verify(reimbursementService).queryReimbursements(Status.PENDING, 3);
    }

    @Test
    void managerQuery_allowsMissingOptionalParams() throws Exception {
        when(reimbursementService.queryReimbursements(null, null)).thenReturn(List.of());

        mockMvc.perform(get("/manager/reimbursements").with(user(manager())))
                .andExpect(status().isOk());

        verify(reimbursementService).queryReimbursements(null, null);
    }

    @Test
    void managerQuery_invalidStatus_returns400() throws Exception {
        mockMvc.perform(get("/manager/reimbursements").with(user(manager())).param("status", "BOGUS"))
                .andExpect(status().isBadRequest());

        verify(reimbursementService, never()).queryReimbursements(any(), any());
    }

    @Test
    void managerQuery_asEmployee_isForbidden() throws Exception {
        mockMvc.perform(get("/manager/reimbursements").with(user(employee())))
                .andExpect(status().isForbidden());

        verify(reimbursementService, never()).queryReimbursements(any(), any());
    }

    // ----------------------------- PATCH /reimbursements/{id} (update) -----------------------------

    @Test
    void updateReimbursement_returns200_onSuccess() throws Exception {
        when(reimbursementService.updateReimbursement(eq(10), any(Reimbursement.class)))
                .thenReturn(new Reimbursement());

        mockMvc.perform(patch("/reimbursements/10").with(user(employee()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isOk());

        verify(reimbursementService).updateReimbursement(eq(10), any(Reimbursement.class));
    }

    @Test
    void updateReimbursement_serviceRejectsResolved_returns400() throws Exception {
        when(reimbursementService.updateReimbursement(eq(10), any(Reimbursement.class)))
                .thenThrow(new IllegalArgumentException("Cannot update a reimbursement that has been approved or denied."));

        mockMvc.perform(patch("/reimbursements/10").with(user(employee()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isBadRequest());
    }

    // ----------------------------- PATCH /manager/reimbursements/{id} (resolve) -----------------------------

    @Test
    void resolveReimbursement_returns200_andPassesManagerAndStatus() throws Exception {
        when(reimbursementService.resolveReimbursement(eq(10), any(User.class), eq(Status.APPROVED)))
                .thenReturn(new Reimbursement());

        mockMvc.perform(patch("/manager/reimbursements/10").with(user(manager()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"APPROVED\"}"))
                .andExpect(status().isOk());

        verify(reimbursementService).resolveReimbursement(eq(10),
                argThat(u -> u.getUserId() == 99), eq(Status.APPROVED));
    }

    @Test
    void resolveReimbursement_missingStatus_returns400() throws Exception {
        mockMvc.perform(patch("/manager/reimbursements/10").with(user(manager()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(reimbursementService, never()).resolveReimbursement(anyInt(), any(), any());
    }

    @Test
    void resolveReimbursement_serviceError_returns400() throws Exception {
        when(reimbursementService.resolveReimbursement(eq(10), any(User.class), eq(Status.DENIED)))
                .thenThrow(new IllegalArgumentException("Reimbursement ID not found."));

        mockMvc.perform(patch("/manager/reimbursements/10").with(user(manager()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"DENIED\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resolveReimbursement_asEmployee_isForbidden() throws Exception {
        mockMvc.perform(patch("/manager/reimbursements/10").with(user(employee()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"APPROVED\"}"))
                .andExpect(status().isForbidden());
    }
}
