package com.rev.g3.i2.ers2.repo;

import com.rev.g3.i2.ers2.enums.Role;
import com.rev.g3.i2.ers2.model.Department;
import com.rev.g3.i2.ers2.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JPA-slice tests for {@link UserDAO}. Covers the derived query {@code findByUsername} and the
 * inherited {@code save}/{@code findById} that {@code UserServiceImp} relies on.
 */
@DataJpaTest
@ActiveProfiles("test")
class UserDAOTest {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private DepartmentDAO departmentDAO;

    private int departmentId;

    @BeforeEach
    void seedDepartment() {
        departmentId = departmentDAO.save(new Department(0, "Engineering")).getDepartmentId();
    }

    private User user(String username) {
        User u = new User();
        u.setUsername(username);
        u.setPassword("$2a$10$hashedhashedhashedhashedhashedhashedhashedhashedhashed");
        u.setFirstName("Test");
        u.setLastName("User");
        u.setRole(Role.EMPLOYEE);
        u.setDepartmentId(departmentId);
        return u;
    }

    @Test
    void save_assignsGeneratedId_andPersistsAllColumns() {
        User saved = userDAO.save(user("alice"));

        assertTrue(saved.getUserId() > 0);
        User reloaded = userDAO.findById(saved.getUserId()).orElseThrow();
        assertEquals("alice", reloaded.getUsername());
        assertEquals("Test", reloaded.getFirstName());
        assertEquals("User", reloaded.getLastName());
        assertEquals(Role.EMPLOYEE, reloaded.getRole());
        assertEquals(departmentId, reloaded.getDepartmentId());
        assertNotNull(reloaded.getPassword(), "stored hash must round-trip");
    }

    @Test
    void findByUsername_returnsMatchingUser() {
        userDAO.save(user("bob"));
        userDAO.save(user("carol"));

        User found = userDAO.findByUsername("bob");

        assertNotNull(found);
        assertEquals("bob", found.getUsername());
    }

    @Test
    void findByUsername_isCaseSensitive_byDefault() {
        userDAO.save(user("Dave"));

        assertNull(userDAO.findByUsername("dave"),
                "derived query does an exact match; login/register rely on this");
    }

    @Test
    void findByUsername_returnsNull_whenAbsent() {
        assertNull(userDAO.findByUsername("nobody"));
    }

    @Test
    void save_updatesExistingRow_whenIdAlreadySet() {
        User saved = userDAO.save(user("erin"));
        saved.setLastName("Renamed");

        userDAO.save(saved);

        assertEquals("Renamed", userDAO.findById(saved.getUserId()).orElseThrow().getLastName());
        assertEquals(1, userDAO.count(), "update must not insert a second row");
    }
}
