package com.rev.g3.i2.ers2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rev.g3.i2.ers2.config.SecurityConfig;
import com.rev.g3.i2.ers2.enums.Role;
import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.enums.Type;
import com.rev.g3.i2.ers2.exception.GlobalExceptionHandler;
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
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-slice tests for {@link ReimbursementHandler} under the real {@link SecurityConfig}.
 * Every authenticated request uses a real {@link UserPrincipal} (via {@code .with(user(...))}) because
 * the controller casts {@code @AuthenticationPrincipal} to UserPrincipal — {@code @WithMockUser} would
 * inject a Spring User and blow up with a ClassCastException.
 */
@WebMvcTest(ReimbursementHandler.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, GlobalExceptionHandler.class})
class ReimbursementHandlerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private ReimbursementService reimbursementService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private UserDetailsService userDetailsService;

    private static UserPrincipal employee(int id) { return principal(id, Role.EMPLOYEE); }
    private static UserPrincipal manager(int id)  { return principal(id, Role.MANAGER); }

    private static UserPrincipal principal(int id, Role role) {
        User u = new User();
        u.setUserId(id);
        u.setUsername(role.name().toLowerCase() + id);
        u.setRole(role);
        return new UserPrincipal(u);
    }

    private static Reimbursement sample() {
        Reimbursement r = new Reimbursement();
        r.setReimbursementId(1);
        r.setAmount(100.0);
        r.setDescription("Taxi");
        r.setType(Type.TRAVEL);
        r.setStatus(Status.PENDING);
        r.setAuthorId(5);
        return r;
    }

    private String json(Object o) throws Exception { return objectMapper.writeValueAsString(o); }

    // ----------------------------- POST /reimbursements -----------------------------

    @Test
    void create_passesAuthenticatedUserAsAuthor() throws Exception {
        when(reimbursementService.createReimbursement(any(), any())).thenReturn(sample());

        mockMvc.perform(post("/reimbursements").with(user(employee(5)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("amount", 100.0, "description", "Taxi", "type", "TRAVEL"))))
                .andExpect(status().isOk());

        verify(reimbursementService).createReimbursement(any(Reimbursement.class), argThat(u -> u.getUserId() == 5));
    }

    @Test
    void create_withoutStatusInBody_isAccepted() throws Exception {
        // status is decided by the service (PENDING); clients never send it
        when(reimbursementService.createReimbursement(any(), any())).thenReturn(sample());

        mockMvc.perform(post("/reimbursements").with(user(employee(5)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("amount", 20.0, "description", "Lunch", "type", "FOOD"))))
                .andExpect(status().isOk());
    }

    @Test
    void create_beanValidationFailure_returns400_withFieldMessages() throws Exception {
        mockMvc.perform(post("/reimbursements").with(user(employee(5)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("amount", -5, "description", "", "type", "FOOD"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(reimbursementService, never()).createReimbursement(any(), any());
    }

    @Test
    void create_amountOverMax_returns400() throws Exception {
        mockMvc.perform(post("/reimbursements").with(user(employee(5)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("amount", 10001, "description", "Jet", "type", "TRAVEL"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_serviceRejects_returns400() throws Exception {
        when(reimbursementService.createReimbursement(any(), any()))
                .thenThrow(new IllegalArgumentException("Reimbursement amount cannot be negative or zero."));

        mockMvc.perform(post("/reimbursements").with(user(employee(5)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("amount", 1, "description", "x", "type", "OTHER"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_unauthenticated_isRejected() throws Exception {
        mockMvc.perform(post("/reimbursements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("amount", 1, "description", "x", "type", "OTHER"))))
                .andExpect(status().is4xxClientError());

        verify(reimbursementService, never()).createReimbursement(any(), any());
    }

    // ----------------------------- GET /reimbursements/{id} -----------------------------

    @Test
    void byAuthor_noFilter_passesNullStatus() throws Exception {
        when(reimbursementService.queryReimbursementsByAuthorId(5, null)).thenReturn(List.of(sample()));

        mockMvc.perform(get("/reimbursements/5").with(user(employee(5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Taxi"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void byAuthor_withStatus_parsesEnum() throws Exception {
        when(reimbursementService.queryReimbursementsByAuthorId(5, Status.APPROVED)).thenReturn(List.of());

        mockMvc.perform(get("/reimbursements/5").param("status", "APPROVED").with(user(employee(5))))
                .andExpect(status().isOk());

        verify(reimbursementService).queryReimbursementsByAuthorId(5, Status.APPROVED);
    }

    @Test
    void byAuthor_invalidStatus_returns400_withoutCallingService() throws Exception {
        mockMvc.perform(get("/reimbursements/5").param("status", "NOPE").with(user(employee(5))))
                .andExpect(status().isBadRequest());

        verify(reimbursementService, never()).queryReimbursementsByAuthorId(anyInt(), any());
    }

    @Test
    void byAuthor_nonNumericId_returns400() throws Exception {
        mockMvc.perform(get("/reimbursements/abc").with(user(employee(5))))
                .andExpect(status().isBadRequest());
    }

    // ----------------------------- GET /manager/reimbursements -----------------------------

    @Test
    void managerQuery_bindsBothQueryParams() throws Exception {
        when(reimbursementService.queryReimbursements(Status.PENDING, 3)).thenReturn(List.of(sample()));

        mockMvc.perform(get("/manager/reimbursements")
                        .param("status", "PENDING").param("departmentId", "3")
                        .with(user(manager(9))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(reimbursementService).queryReimbursements(Status.PENDING, 3);
    }

    @Test
    void managerQuery_missingParams_passesNulls() throws Exception {
        when(reimbursementService.queryReimbursements(null, null)).thenReturn(List.of());

        mockMvc.perform(get("/manager/reimbursements").with(user(manager(9))))
                .andExpect(status().isOk());

        verify(reimbursementService).queryReimbursements(null, null);
    }

    @Test
    void managerQuery_invalidStatus_returns400() throws Exception {
        mockMvc.perform(get("/manager/reimbursements").param("status", "BOGUS").with(user(manager(9))))
                .andExpect(status().isBadRequest());

        verify(reimbursementService, never()).queryReimbursements(any(), any());
    }

    @Test
    void managerQuery_nonNumericDepartment_returns400() throws Exception {
        mockMvc.perform(get("/manager/reimbursements").param("departmentId", "x").with(user(manager(9))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void managerQuery_employee_isForbidden() throws Exception {
        mockMvc.perform(get("/manager/reimbursements").with(user(employee(5))))
                .andExpect(status().isForbidden());

        verify(reimbursementService, never()).queryReimbursements(any(), any());
    }

    // ----------------------------- PATCH /reimbursements/{id} -----------------------------

    @Test
    void update_returns200_andKeysOffPathId() throws Exception {
        when(reimbursementService.updateReimbursement(eq(10), any())).thenReturn(sample());

        mockMvc.perform(patch("/reimbursements/10").with(user(employee(5)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("amount", 300.0, "description", "Hotel", "type", "LODGING"))))
                .andExpect(status().isOk());

        verify(reimbursementService).updateReimbursement(eq(10), argThat(r -> r.getAmount() == 300.0));
    }

    @Test
    void update_alreadyResolved_returns400() throws Exception {
        when(reimbursementService.updateReimbursement(eq(10), any()))
                .thenThrow(new IllegalArgumentException("Cannot update a reimbursement that has been approved or denied."));

        mockMvc.perform(patch("/reimbursements/10").with(user(employee(5)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("amount", 300.0, "description", "Hotel", "type", "LODGING"))))
                .andExpect(status().isBadRequest());
    }

    // ----------------------------- PATCH /manager/reimbursements/{id} -----------------------------

    @Test
    void resolve_statusOnlyBody_callsResolveWithManager() throws Exception {
        when(reimbursementService.resolveReimbursement(eq(10), any(), eq(Status.APPROVED))).thenReturn(sample());

        mockMvc.perform(patch("/manager/reimbursements/10").with(user(manager(9)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "APPROVED"))))
                .andExpect(status().isOk());

        verify(reimbursementService).resolveReimbursement(eq(10), argThat(m -> m.getUserId() == 9), eq(Status.APPROVED));
        verify(reimbursementService, never()).updateReimbursement(anyInt(), any());
    }

    @Test
    void resolve_missingStatus_returns400() throws Exception {
        mockMvc.perform(patch("/manager/reimbursements/10").with(user(manager(9)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(reimbursementService, never()).resolveReimbursement(anyInt(), any(), any());
    }

    @Test
    void resolve_serviceRejectsPending_returns400() throws Exception {
        when(reimbursementService.resolveReimbursement(eq(10), any(), eq(Status.PENDING)))
                .thenThrow(new IllegalArgumentException("Cannot set status to PENDING when resolving a reimbursement."));

        mockMvc.perform(patch("/manager/reimbursements/10").with(user(manager(9)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "PENDING"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resolve_employee_isForbidden() throws Exception {
        mockMvc.perform(patch("/manager/reimbursements/10").with(user(employee(5)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "APPROVED"))))
                .andExpect(status().isForbidden());

        verify(reimbursementService, never()).resolveReimbursement(anyInt(), any(), any());
    }
}
