package com.rev.g3.i2.ers2.repo;

import com.rev.g3.i2.ers2.model.Department;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JPA-slice tests for {@link DepartmentDAO} against in-memory H2 (see application-test.properties).
 * Each test runs in a rolled-back transaction, so tests do not see each other's rows.
 */
@DataJpaTest
@ActiveProfiles("test")
class DepartmentDAOTest {

    @Autowired
    private DepartmentDAO departmentDAO;

    @Test
    void save_assignsGeneratedId() {
        Department saved = departmentDAO.save(new Department(0, "Engineering"));

        assertTrue(saved.getDepartmentId() > 0, "IDENTITY strategy must assign an id on save");
        assertEquals("Engineering", saved.getDepartmentName());
    }

    @Test
    void findByDepartmentId_returnsMatchingRow() {
        Department saved = departmentDAO.save(new Department(0, "Finance"));

        Department found = departmentDAO.findByDepartmentId(saved.getDepartmentId());

        assertNotNull(found);
        assertEquals(saved.getDepartmentId(), found.getDepartmentId());
        assertEquals("Finance", found.getDepartmentName());
    }

    @Test
    void findByDepartmentId_returnsNull_whenAbsent() {
        assertNull(departmentDAO.findByDepartmentId(999_999));
    }

    @Test
    void findAll_returnsEveryDepartment() {
        departmentDAO.save(new Department(0, "Engineering"));
        departmentDAO.save(new Department(0, "Finance"));
        departmentDAO.save(new Department(0, "HR"));

        List<Department> all = departmentDAO.findAll();

        assertEquals(3, all.size());
        assertTrue(all.stream().map(Department::getDepartmentName).toList()
                .containsAll(List.of("Engineering", "Finance", "HR")));
    }

    @Test
    void findAll_returnsEmptyList_whenTableEmpty() {
        assertTrue(departmentDAO.findAll().isEmpty());
    }
}
