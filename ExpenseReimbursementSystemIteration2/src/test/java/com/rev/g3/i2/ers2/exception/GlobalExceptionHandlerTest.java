package com.rev.g3.i2.ers2.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Direct unit tests for {@link GlobalExceptionHandler}'s status/message mapping. The controller-slice
 * tests already prove Spring routes exceptions here; these pin the body shape without booting MVC.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/register");

    @Test
    void usernameAlreadyExists_maps409() {
        ResponseEntity<ErrorResponse> r = handler.handleUsernameAlreadyExists(
                new UsernameAlreadyExistsException("bob"), request);

        assertEquals(HttpStatus.CONFLICT, r.getStatusCode());
        assertEquals(409, r.getBody().getStatus());
        assertEquals("Username already exist: bob", r.getBody().getMessage());
        assertEquals("/register", r.getBody().getPath());
        assertNotNull(r.getBody().getTimestamp());
    }

    @Test
    void departmentNotFound_maps404() {
        ResponseEntity<ErrorResponse> r = handler.handleDepartmentNotFound(
                new DepartmentNotFoundException(42), request);

        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
        assertEquals("Department not found with ID: 42", r.getBody().getMessage());
    }

    @Test
    void illegalArgument_maps400_withOriginalMessage() {
        ResponseEntity<ErrorResponse> r = handler.handleIllegalArgument(
                new IllegalArgumentException("Password cannot be null or blank."), request);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals("Password cannot be null or blank.", r.getBody().getMessage());
    }

    @Test
    void badCredentials_maps401_withGenericMessage() {
        ResponseEntity<ErrorResponse> r = handler.handleBadCredentials(
                new BadCredentialsException("internal detail"), request);

        assertEquals(HttpStatus.UNAUTHORIZED, r.getStatusCode());
        assertEquals("Invalid username or password.", r.getBody().getMessage(),
                "must not leak whether the username or password was wrong");
    }

    @Test
    void unexpected_maps500_andHidesDetail() {
        ResponseEntity<ErrorResponse> r = handler.handleAll(new RuntimeException("db password is hunter2"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, r.getStatusCode());
        assertFalse(r.getBody().getMessage().contains("hunter2"));
        assertEquals("Internal Server Error", r.getBody().getError());
    }
}
