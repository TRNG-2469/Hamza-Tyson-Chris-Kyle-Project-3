package com.rev.g3.i2.ers2.security;

import com.rev.g3.i2.ers2.enums.Role;
import com.rev.g3.i2.ers2.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UserPrincipal}, the {@code UserDetails} adapter over {@link User}.
 *

 * as shipped has no {@code role} field/getter (only a JPA {@code @DiscriminatorColumn(name="role")}).
 * A {@code Role role} property with a getter/setter must be added to {@link User} for these to compile
 * and pass. The setRole(...) calls below assume that field exists.
 */
class UserPrincipalTest {

    private User user(String username, String password, Role role) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        // PRECONDITION: requires User.setRole(Role)
        u.setRole(role);
        return u;
    }

    @Test
    void getAuthorities_prefixesRoleWithROLE_forEmployee() {
        UserPrincipal p = new UserPrincipal(user("emp", "hash", Role.EMPLOYEE));

        Collection<? extends GrantedAuthority> auths = p.getAuthorities();
        assertEquals(1, auths.size());
        assertTrue(auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE")));
    }

    @Test
    void getAuthorities_prefixesRoleWithROLE_forManager() {
        UserPrincipal p = new UserPrincipal(user("mgr", "hash", Role.MANAGER));

        assertTrue(p.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER")));
    }

    @Test
    void getUsername_returnsUnderlyingUsername() {
        UserPrincipal p = new UserPrincipal(user("carol", "hash", Role.EMPLOYEE));
        assertEquals("carol", p.getUsername());
    }

    @Test
    void getPassword_returnsUnderlyingHash() {
        UserPrincipal p = new UserPrincipal(user("carol", "bcrypt-hash", Role.EMPLOYEE));
        assertEquals("bcrypt-hash", p.getPassword());
    }

    @Test
    void getUser_returnsWrappedInstance() {
        User u = user("carol", "hash", Role.EMPLOYEE);
        UserPrincipal p = new UserPrincipal(u);
        assertSame(u, p.getUser());
    }
}
