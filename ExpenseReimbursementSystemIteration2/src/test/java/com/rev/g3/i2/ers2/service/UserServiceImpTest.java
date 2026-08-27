package com.rev.g3.i2.ers2.service;

import com.rev.g3.i2.ers2.exception.DepartmentNotFoundException;
import com.rev.g3.i2.ers2.exception.UsernameAlreadyExistsException;
import com.rev.g3.i2.ers2.model.Department;
import com.rev.g3.i2.ers2.model.User;
import com.rev.g3.i2.ers2.repo.DepartmentDAO;
import com.rev.g3.i2.ers2.repo.UserDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserServiceImp}. All collaborators ({@link UserDAO}, {@link DepartmentDAO})
 * are mocked; only the service's own logic (validation, ordering of checks, bcrypt hashing, and
 * password stripping) is under test.
 *
 * A helper builds a fully-populated valid {@link User} so each test can mutate exactly one field to
 * isolate the branch it targets.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private DepartmentDAO departmentDAO;

    @InjectMocks
    private UserServiceImp userService;

    private User validUser() {
        User u = new User();
        u.setUserId(0);
        u.setUsername("newuser");
        u.setPassword("Password1");
        u.setFirstName("New");
        u.setLastName("User");
        u.setDepartmentId(1);
        return u;
    }

    // ----------------------------- searchByUsername -----------------------------

    @Test
    void searchByUsername_delegatesToDao_andReturnsUser() {
        User stored = validUser();
        when(userDAO.findByUsername("newuser")).thenReturn(stored);

        assertSame(stored, userService.searchByUsername("newuser"));
        verify(userDAO).findByUsername("newuser");
    }

    @Test
    void searchByUsername_returnsNull_whenDaoFindsNothing() {
        when(userDAO.findByUsername("ghost")).thenReturn(null);

        assertNull(userService.searchByUsername("ghost"));
    }

    @Test
    void searchByUsername_throwsIllegalArgument_forNull() {
        assertThrows(IllegalArgumentException.class, () -> userService.searchByUsername(null));
        verifyNoInteractions(userDAO);
    }

    @Test
    void searchByUsername_throwsIllegalArgument_forEmpty() {
        assertThrows(IllegalArgumentException.class, () -> userService.searchByUsername(""));
        verifyNoInteractions(userDAO);
    }

    // ----------------------------- login -----------------------------

    @Test
    void login_returnsUserWithPasswordStripped_onCorrectPassword() {
        String hash = BCrypt.hashpw("Password1", BCrypt.gensalt());
        User stored = validUser();
        stored.setPassword(hash);
        when(userDAO.findByUsername("newuser")).thenReturn(stored);

        User result = userService.login("newuser", "Password1");

        assertNotNull(result);
        assertNull(result.getPassword(), "password must be nulled out before returning to caller");
        assertEquals("newuser", result.getUsername());
    }

    @Test
    void login_returnsNull_onWrongPassword() {
        String hash = BCrypt.hashpw("Password1", BCrypt.gensalt());
        User stored = validUser();
        stored.setPassword(hash);
        when(userDAO.findByUsername("newuser")).thenReturn(stored);

        assertNull(userService.login("newuser", "WrongPassword"));
    }

    @Test
    void login_returnsNull_whenUserNotFound() {
        when(userDAO.findByUsername("ghost")).thenReturn(null);

        assertNull(userService.login("ghost", "whatever"));
    }

    @Test
    void login_returnsNull_whenStoredPasswordIsNull() {
        User stored = validUser();
        stored.setPassword(null);
        when(userDAO.findByUsername("newuser")).thenReturn(stored);

        assertNull(userService.login("newuser", "Password1"));
    }

    @Test
    void login_throwsIllegalArgument_forNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> userService.login("newuser", null));
    }

    @Test
    void login_throwsIllegalArgument_forBlankPassword() {
        assertThrows(IllegalArgumentException.class, () -> userService.login("newuser", "   "));
    }

    @Test
    void login_throwsIllegalArgument_whenUsernameNullAfterPasswordCheck() {
        // password check passes (non-blank), then searchByUsername(null) must reject.
        assertThrows(IllegalArgumentException.class, () -> userService.login(null, "Password1"));
    }

    // ----------------------------- register -----------------------------

    @Test
    void register_hashesPassword_persists_andStripsPasswordOnReturn() {
        User input = validUser();
        when(userDAO.findByUsername("newuser")).thenReturn(null);
        when(departmentDAO.findByDepartmentId(1)).thenReturn(new Department(1, "Engineering"));
        // DAO returns the same entity it was handed (typical save() behavior)
        when(userDAO.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.register(input);

        assertNotNull(result);
        assertNull(result.getPassword(), "returned user must not carry a password");

        // Capture what was actually saved and assert the stored password was a bcrypt hash of the raw input.
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDAO).save(captor.capture());
        String savedHash = captor.getValue().getPassword();
        assertNotNull(savedHash);
        assertNotEquals("Password1", savedHash, "raw password must never be persisted");
        assertTrue(BCrypt.checkpw("Password1", savedHash), "persisted value must verify against the raw password");
    }

    @Test
    void register_throwsUsernameAlreadyExists_whenDuplicate() {
        User input = validUser();
        when(userDAO.findByUsername("newuser")).thenReturn(validUser()); // already taken

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.register(input));
        verify(userDAO, never()).save(any());
    }

    @Test
    void register_throwsDepartmentNotFound_whenDepartmentMissing() {
        User input = validUser();
        when(userDAO.findByUsername("newuser")).thenReturn(null);
        when(departmentDAO.findByDepartmentId(1)).thenReturn(null);

        assertThrows(DepartmentNotFoundException.class, () -> userService.register(input));
        verify(userDAO, never()).save(any());
    }

    @Test
    void register_throwsIllegalArgument_forNullUser() {
        assertThrows(IllegalArgumentException.class, () -> userService.register(null));
    }

    @Test
    void register_throwsIllegalArgument_forBlankPassword() {
        User input = validUser();
        input.setPassword("  ");
        when(userDAO.findByUsername("newuser")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.register(input));
        verify(userDAO, never()).save(any());
    }

    @Test
    void register_throwsIllegalArgument_forNullPassword() {
        User input = validUser();
        input.setPassword(null);
        when(userDAO.findByUsername("newuser")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.register(input));
    }

    @Test
    void register_throwsIllegalArgument_forBlankFirstName() {
        User input = validUser();
        input.setFirstName("   ");
        when(userDAO.findByUsername("newuser")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.register(input));
    }

    @Test
    void register_throwsIllegalArgument_forNullLastName() {
        User input = validUser();
        input.setLastName(null);
        when(userDAO.findByUsername("newuser")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.register(input));
    }

    @Test
    void register_checksDuplicateBeforePersisting() {
        // Ordering guard: duplicate check must short-circuit before any save.
        User input = validUser();
        when(userDAO.findByUsername("newuser")).thenReturn(validUser());

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.register(input));
        verify(departmentDAO, never()).findByDepartmentId(anyInt());
        verify(userDAO, never()).save(any());
    }
}
