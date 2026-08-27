package com.rev.g3.i2.ers2.repo;

import com.rev.g3.i2.ers2.enums.Role;
import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.enums.Type;
import com.rev.g3.i2.ers2.model.Department;
import com.rev.g3.i2.ers2.model.Reimbursement;
import com.rev.g3.i2.ers2.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JPA-slice tests for {@link ReimbursementDAO}, in particular the two custom {@code @Query}
 * methods with optional (nullable) filter parameters.
 *
 * Fixture: two departments, one employee in each, and four reimbursements:
 *   emp1 (dept A): PENDING, APPROVED
 *   emp2 (dept B): PENDING, DENIED
 */
@DataJpaTest
@ActiveProfiles("test")
class ReimbursementDAOTest {

    @Autowired private ReimbursementDAO reimbursementDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private DepartmentDAO departmentDAO;

    private int deptA, deptB;
    private int emp1, emp2;

    @BeforeEach
    void seed() {
        deptA = departmentDAO.save(new Department(0, "Engineering")).getDepartmentId();
        deptB = departmentDAO.save(new Department(0, "Finance")).getDepartmentId();
        emp1 = userDAO.save(user("emp1", deptA)).getUserId();
        emp2 = userDAO.save(user("emp2", deptB)).getUserId();

        reimbursementDAO.save(reimbursement(emp1, "Taxi", Status.PENDING));
        reimbursementDAO.save(reimbursement(emp1, "Hotel", Status.APPROVED));
        reimbursementDAO.save(reimbursement(emp2, "Lunch", Status.PENDING));
        reimbursementDAO.save(reimbursement(emp2, "Flight", Status.DENIED));
    }

    private static User user(String username, int departmentId) {
        User u = new User();
        u.setUsername(username);
        u.setPassword("hash");
        u.setFirstName("First");
        u.setLastName("Last");
        u.setRole(Role.EMPLOYEE);
        u.setDepartmentId(departmentId);
        return u;
    }

    private static Reimbursement reimbursement(int authorId, String description, Status status) {
        Reimbursement r = new Reimbursement();
        r.setAmount(100.0);
        r.setDescription(description);
        r.setType(Type.TRAVEL);
        r.setStatus(status);
        r.setAuthorId(authorId);
        return r;
    }

    private static List<String> descriptions(List<Reimbursement> list) {
        return list.stream().map(Reimbursement::getDescription).sorted().toList();
    }

    // ----------------------------- save / findById -----------------------------

    @Test
    void save_assignsId_andRoundTripsEnumsAndNullableResolver() {
        Reimbursement saved = reimbursementDAO.save(reimbursement(emp1, "Conference", Status.PENDING));

        assertTrue(saved.getReimbursementId() > 0);
        Reimbursement reloaded = reimbursementDAO.findById(saved.getReimbursementId()).orElseThrow();
        assertEquals(Type.TRAVEL, reloaded.getType());
        assertEquals(Status.PENDING, reloaded.getStatus());
        assertEquals(emp1, reloaded.getAuthorId());
        assertNull(reloaded.getResolverId(), "resolver is null until a manager resolves it");
    }

    @Test
    void save_persistsResolverId_whenSet() {
        Reimbursement r = reimbursement(emp1, "Resolved", Status.APPROVED);
        r.setResolverId(emp2);
        int id = reimbursementDAO.save(r).getReimbursementId();

        assertEquals(emp2, reimbursementDAO.findById(id).orElseThrow().getResolverId());
    }

    // ----------------------------- queryReimbursementsByAuthorId -----------------------------

    @Test
    void queryByAuthor_nullStatus_returnsAllForThatAuthorOnly() {
        List<Reimbursement> result = reimbursementDAO.queryReimbursementsByAuthorId(emp1, null);

        assertEquals(List.of("Hotel", "Taxi"), descriptions(result));
        assertTrue(result.stream().allMatch(r -> r.getAuthorId() == emp1));
    }

    @Test
    void queryByAuthor_withStatus_filtersWithinAuthor() {
        List<Reimbursement> result = reimbursementDAO.queryReimbursementsByAuthorId(emp1, Status.APPROVED);

        assertEquals(List.of("Hotel"), descriptions(result));
    }

    @Test
    void queryByAuthor_returnsEmpty_whenStatusNotPresentForAuthor() {
        assertTrue(reimbursementDAO.queryReimbursementsByAuthorId(emp1, Status.DENIED).isEmpty());
    }

    @Test
    void queryByAuthor_returnsEmpty_forUnknownAuthor() {
        assertTrue(reimbursementDAO.queryReimbursementsByAuthorId(999_999, null).isEmpty());
    }

    // ----------------------------- queryReimbursements (manager view) -----------------------------

    @Test
    void queryReimbursements_noFilters_returnsEverything() {
        List<Reimbursement> result = reimbursementDAO.queryReimbursements(null, null);

        assertEquals(4, result.size());
    }

    @Test
    void queryReimbursements_statusOnly_filtersAcrossAllAuthors() {
        List<Reimbursement> result = reimbursementDAO.queryReimbursements(Status.PENDING, null);

        assertEquals(List.of("Lunch", "Taxi"), descriptions(result));
    }

    @Test
    void queryReimbursements_departmentOnly_returnsThatDepartmentsAuthors() {
        List<Reimbursement> result = reimbursementDAO.queryReimbursements(null, deptB);

        assertEquals(List.of("Flight", "Lunch"), descriptions(result));
    }

    @Test
    void queryReimbursements_bothFilters_intersects() {
        List<Reimbursement> result = reimbursementDAO.queryReimbursements(Status.PENDING, deptA);

        assertEquals(List.of("Taxi"), descriptions(result));
    }

    @Test
    void queryReimbursements_returnsEmpty_forUnknownDepartment() {
        assertTrue(reimbursementDAO.queryReimbursements(null, 999_999).isEmpty());
    }
}
