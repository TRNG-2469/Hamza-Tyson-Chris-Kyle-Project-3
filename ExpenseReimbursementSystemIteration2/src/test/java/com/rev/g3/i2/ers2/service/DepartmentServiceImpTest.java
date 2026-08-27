package com.rev.g3.i2.ers2.service;

import com.rev.g3.i2.ers2.model.Department;
import com.rev.g3.i2.ers2.repo.DepartmentDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link DepartmentServiceImp}. {@link DepartmentDAO} is mocked. The service is a thin
 * pass-through with one guard (positive id), so tests cover that guard plus delegation.
 */
@ExtendWith(MockitoExtension.class)
class DepartmentServiceImpTest {

    @Mock
    private DepartmentDAO departmentDAO;

    @InjectMocks
    private DepartmentServiceImp service;

    @Test
    void queryById_returnsDepartment_whenFound() {
        Department d = new Department(1, "Engineering");
        when(departmentDAO.findByDepartmentId(1)).thenReturn(d);

        assertSame(d, service.findByDepartmentId(1));
        verify(departmentDAO).findByDepartmentId(1);
    }

    @Test
    void queryById_returnsNull_whenDaoReturnsNull() {
        when(departmentDAO.findByDepartmentId(99)).thenReturn(null);

        assertNull(service.findByDepartmentId(99));
    }

    @Test
    void queryById_throwsIllegalArgument_forZeroId() {
        assertThrows(IllegalArgumentException.class, () -> service.findByDepartmentId(0));
        verify(departmentDAO, never()).findByDepartmentId(anyInt());
    }

    @Test
    void queryById_throwsIllegalArgument_forNegativeId() {
        assertThrows(IllegalArgumentException.class, () -> service.findByDepartmentId(-5));
        verify(departmentDAO, never()).findByDepartmentId(anyInt());
    }

    @Test
    void queryDepartments_returnsWholeList() {
        List<Department> all = List.of(new Department(1, "Engineering"), new Department(2, "Finance"));
        when(departmentDAO.findAll()).thenReturn(all);

        List<Department> result = service.queryDepartments();

        assertEquals(2, result.size());
        assertSame(all, result);
    }

    @Test
    void queryDepartments_returnsEmptyList_whenNoneExist() {
        when(departmentDAO.findAll()).thenReturn(List.of());

        assertTrue(service.queryDepartments().isEmpty());
    }
}
