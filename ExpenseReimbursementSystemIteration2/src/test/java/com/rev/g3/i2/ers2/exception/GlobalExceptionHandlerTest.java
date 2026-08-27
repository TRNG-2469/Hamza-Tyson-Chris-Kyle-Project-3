package com.rev.g3.i2.ers2.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Direct unit tests for {@link GlobalExceptionHandler}. No MVC layer — each handler method is invoked
 * with a stub {@link HttpServletRequest} and the returned {@link ResponseEntity} / {@link ErrorResponse}
 * is asserted for status code, reason phrase, message, and path. These complement the controller slice
 * tests, which verify the same mappings end-to-end.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/some/path");
    }

    @Test
    void usernameAlreadyExists_maps409_withMessageAndPath() {
        ResponseEntity<ErrorResponse> resp =
                handler.handleUsernameAlreadyExists(new UsernameAlreadyExistsException("bob"), request);

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        ErrorResponse body = resp.getBody();
        assertNotNull(body);
        assertEquals(409, body.getStatus());
        assertEquals("Conflict", body.getError());
        assertTrue(body.getMessage().contains("bob"));
        assertEquals("/some/path", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void departmentNotFound_maps404() {
        ResponseEntity<ErrorResponse> resp =
                handler.handleDepartmentNotFound(new DepartmentNotFoundException(77), request);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals(404, resp.getBody().getStatus());
        assertTrue(resp.getBody().getMessage().contains("77"));
    }

    @Test
    void illegalArgument_maps400_withOriginalMessage() {
        ResponseEntity<ErrorResponse> resp =
                handler.handleIllegalArgument(new IllegalArgumentException("bad input"), request);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("bad input", resp.getBody().getMessage());
    }

    @Test
    void unhandledException_maps500_withGenericMessage_notLeakingDetail() {
        ResponseEntity<ErrorResponse> resp =
                handler.handleAll(new RuntimeException("stack trace secret"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals(500, resp.getBody().getStatus());
        assertFalse(resp.getBody().getMessage().contains("secret"),
                "internal detail must not be exposed to the client");
    }

    @Test
    void errorResponse_gettersReflectConstructorArgs() {
        // Guards the DTO used by every handler above.
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        ErrorResponse er = new ErrorResponse(now, 418, "I'm a teapot", "short and stout", "/tea");

        assertEquals(now, er.getTimestamp());
        assertEquals(418, er.getStatus());
        assertEquals("I'm a teapot", er.getError());
        assertEquals("short and stout", er.getMessage());
        assertEquals("/tea", er.getPath());
    }
}
