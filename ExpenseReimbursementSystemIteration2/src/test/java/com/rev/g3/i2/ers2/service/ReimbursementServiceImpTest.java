package com.rev.g3.i2.ers2.service;

import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.enums.Type;
import com.rev.g3.i2.ers2.model.Reimbursement;
import com.rev.g3.i2.ers2.model.User;
import com.rev.g3.i2.ers2.repo.ReimbursementDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReimbursementServiceImp}. {@link ReimbursementDAO} is mocked. Focus areas:
 *  - createReimbursement stamps author + PENDING and validates amount/description
 *  - id-argument guards on the read/update paths
 *  - update refuses already-resolved (APPROVED/DENIED) reimbursements and keys off the path id
 *  - resolveReimbursement refuses PENDING target status and stamps the resolver
 */
@ExtendWith(MockitoExtension.class)
class ReimbursementServiceImpTest {

    @Mock
    private ReimbursementDAO reimbursementDAO;

    @InjectMocks
    private ReimbursementServiceImp service;

    private User author(int id) {
        User u = new User();
        u.setUserId(id);
        u.setUsername("author" + id);
        return u;
    }

    private Reimbursement pending() {
        Reimbursement r = new Reimbursement();
        r.setReimbursementId(10);
        r.setAmount(250.0);
        r.setDescription("Client dinner");
        r.setType(Type.FOOD);
        r.setStatus(Status.PENDING);
        r.setAuthorId(5);
        return r;
    }

    // ----------------------------- createReimbursement -----------------------------

    @Test
    void create_stampsAuthorId_andForcesPendingStatus() {
        Reimbursement input = pending();
        input.setStatus(Status.APPROVED); // caller tries to sneak a non-pending status in
        when(reimbursementDAO.save(any(Reimbursement.class))).thenAnswer(i -> i.getArgument(0));

        service.createReimbursement(input, author(42));

        ArgumentCaptor<Reimbursement> captor = ArgumentCaptor.forClass(Reimbursement.class);
        verify(reimbursementDAO).save(captor.capture());
        assertEquals(42, captor.getValue().getAuthorId(), "author id must come from the authenticated user");
        assertEquals(Status.PENDING, captor.getValue().getStatus(), "new reimbursements are always PENDING");
    }

    @Test
    void create_throwsIllegalArgument_forNonPositiveAmount() {
        Reimbursement input = pending();
        input.setAmount(0.0);

        assertThrows(IllegalArgumentException.class,
                () -> service.createReimbursement(input, author(1)));
        verify(reimbursementDAO, never()).save(any());
    }

    @Test
    void create_throwsIllegalArgument_forNegativeAmount() {
        Reimbursement input = pending();
        input.setAmount(-50.0);

        assertThrows(IllegalArgumentException.class,
                () -> service.createReimbursement(input, author(1)));
    }

    @Test
    void create_throwsIllegalArgument_forBlankDescription() {
        Reimbursement input = pending();
        input.setDescription("   ");

        assertThrows(IllegalArgumentException.class,
                () -> service.createReimbursement(input, author(1)));
    }

    @Test
    void create_throwsIllegalArgument_forNullDescription() {
        Reimbursement input = pending();
        input.setDescription(null);

        assertThrows(IllegalArgumentException.class,
                () -> service.createReimbursement(input, author(1)));
    }

    // ----------------------------- queryReimbursementByReimbursementId -----------------------------

    @Test
    void queryById_returnsEntity_whenFound() {
        Reimbursement r = pending();
        when(reimbursementDAO.findById(10)).thenReturn(Optional.of(r));

        assertSame(r, service.queryReimbursementByReimbursementId(10));
    }

    @Test
    void queryById_returnsNull_whenNotFound() {
        when(reimbursementDAO.findById(10)).thenReturn(Optional.empty());

        assertNull(service.queryReimbursementByReimbursementId(10));
    }

    @Test
    void queryById_throwsIllegalArgument_forZeroOrNegativeId() {
        assertThrows(IllegalArgumentException.class, () -> service.queryReimbursementByReimbursementId(0));
        assertThrows(IllegalArgumentException.class, () -> service.queryReimbursementByReimbursementId(-3));
        verify(reimbursementDAO, never()).findById(any());
    }

    // ----------------------------- queryReimbursementsByAuthorId -----------------------------

    @Test
    void queryByAuthor_delegatesToDao() {
        List<Reimbursement> expected = List.of(pending());
        when(reimbursementDAO.queryReimbursementsByAuthorId(5, Status.PENDING)).thenReturn(expected);

        assertSame(expected, service.queryReimbursementsByAuthorId(5, Status.PENDING));
    }

    @Test
    void queryByAuthor_passesNullStatusThrough() {
        when(reimbursementDAO.queryReimbursementsByAuthorId(5, null)).thenReturn(List.of());

        assertTrue(service.queryReimbursementsByAuthorId(5, null).isEmpty());
        verify(reimbursementDAO).queryReimbursementsByAuthorId(5, null);
    }

    @Test
    void queryByAuthor_throwsIllegalArgument_forZeroOrNegativeAuthorId() {
        assertThrows(IllegalArgumentException.class, () -> service.queryReimbursementsByAuthorId(0, null));
        assertThrows(IllegalArgumentException.class, () -> service.queryReimbursementsByAuthorId(-1, null));
    }

    // ----------------------------- queryReimbursements (manager view) -----------------------------

    @Test
    void queryReimbursements_delegatesWithBothFilters() {
        List<Reimbursement> expected = List.of(pending());
        when(reimbursementDAO.queryReimbursements(Status.PENDING, 7)).thenReturn(expected);

        assertSame(expected, service.queryReimbursements(Status.PENDING, 7));
    }

    @Test
    void queryReimbursements_allowsNullFilters() {
        when(reimbursementDAO.queryReimbursements(null, null)).thenReturn(List.of());

        assertNotNull(service.queryReimbursements(null, null));
        verify(reimbursementDAO).queryReimbursements(null, null);
    }

    // ----------------------------- updateReimbursement -----------------------------

    @Test
    void update_persists_whenOriginalIsPending() {
        Reimbursement incoming = pending();
        incoming.setAmount(300.0);
        Reimbursement original = pending(); // status PENDING
        when(reimbursementDAO.findById(10)).thenReturn(Optional.of(original));
        when(reimbursementDAO.save(any(Reimbursement.class))).thenAnswer(i -> i.getArgument(0));

        service.updateReimbursement(10, incoming);

        ArgumentCaptor<Reimbursement> captor = ArgumentCaptor.forClass(Reimbursement.class);
        verify(reimbursementDAO).save(captor.capture());
        assertEquals(10, captor.getValue().getReimbursementId(),
                "id must be taken from the path argument, not the body");
    }

    @Test
    void update_throwsIllegalArgument_forZeroOrNegativeId() {
        assertThrows(IllegalArgumentException.class, () -> service.updateReimbursement(0, pending()));
        assertThrows(IllegalArgumentException.class, () -> service.updateReimbursement(-2, pending()));
    }

    @Test
    void update_throwsIllegalArgument_whenAmountInvalid() {
        Reimbursement incoming = pending();
        incoming.setAmount(0.0);

        assertThrows(IllegalArgumentException.class, () -> service.updateReimbursement(10, incoming));
        verify(reimbursementDAO, never()).save(any());
    }

    @Test
    void update_throwsIllegalArgument_whenTypeIsNull() {
        Reimbursement incoming = pending();
        incoming.setType(null);
        // validation() passes (amount/description ok); the explicit null type/status check must fire.
        assertThrows(IllegalArgumentException.class, () -> service.updateReimbursement(10, incoming));
    }

    @Test
    void update_throwsIllegalArgument_whenStatusIsNull() {
        Reimbursement incoming = pending();
        incoming.setStatus(null);

        assertThrows(IllegalArgumentException.class, () -> service.updateReimbursement(10, incoming));
    }

    @Test
    void update_throwsIllegalArgument_whenOriginalNotFound() {
        Reimbursement incoming = pending();
        when(reimbursementDAO.findById(10)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.updateReimbursement(10, incoming));
        verify(reimbursementDAO, never()).save(any());
    }

    @Test
    void update_rejectsAlreadyApproved() {
        Reimbursement incoming = pending();
        Reimbursement original = pending();
        original.setStatus(Status.APPROVED);
        when(reimbursementDAO.findById(10)).thenReturn(Optional.of(original));

        assertThrows(IllegalArgumentException.class, () -> service.updateReimbursement(10, incoming));
        verify(reimbursementDAO, never()).save(any());
    }

    @Test
    void update_rejectsAlreadyDenied() {
        Reimbursement incoming = pending();
        Reimbursement original = pending();
        original.setStatus(Status.DENIED);
        when(reimbursementDAO.findById(10)).thenReturn(Optional.of(original));

        assertThrows(IllegalArgumentException.class, () -> service.updateReimbursement(10, incoming));
    }

    // ----------------------------- resolveReimbursement -----------------------------

    @Test
    void resolve_setsStatusAndResolver_whenPendingAndTargetApproved() {
        Reimbursement original = pending(); // PENDING
        when(reimbursementDAO.findById(10)).thenReturn(Optional.of(original));
        when(reimbursementDAO.save(any(Reimbursement.class))).thenAnswer(i -> i.getArgument(0));

        service.resolveReimbursement(10, author(99), Status.APPROVED);

        ArgumentCaptor<Reimbursement> captor = ArgumentCaptor.forClass(Reimbursement.class);
        verify(reimbursementDAO).save(captor.capture());
        assertEquals(Status.APPROVED, captor.getValue().getStatus());
        assertEquals(99, captor.getValue().getResolverId(), "resolver id must be the acting manager");
    }

    @Test
    void resolve_allowsDenied() {
        Reimbursement original = pending();
        when(reimbursementDAO.findById(10)).thenReturn(Optional.of(original));
        when(reimbursementDAO.save(any(Reimbursement.class))).thenAnswer(i -> i.getArgument(0));

        service.resolveReimbursement(10, author(99), Status.DENIED);

        ArgumentCaptor<Reimbursement> captor = ArgumentCaptor.forClass(Reimbursement.class);
        verify(reimbursementDAO).save(captor.capture());
        assertEquals(Status.DENIED, captor.getValue().getStatus());
    }

    @Test
    void resolve_throwsIllegalArgument_whenTargetStatusIsPending() {
        assertThrows(IllegalArgumentException.class,
                () -> service.resolveReimbursement(10, author(99), Status.PENDING));
        verify(reimbursementDAO, never()).save(any());
    }

    @Test
    void resolve_throwsIllegalArgument_whenOriginalNotFound() {
        when(reimbursementDAO.findById(10)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.resolveReimbursement(10, author(99), Status.APPROVED));
    }

    @Test
    void resolve_rejectsAlreadyApproved() {
        Reimbursement original = pending();
        original.setStatus(Status.APPROVED);
        when(reimbursementDAO.findById(10)).thenReturn(Optional.of(original));

        assertThrows(IllegalArgumentException.class,
                () -> service.resolveReimbursement(10, author(99), Status.DENIED));
        verify(reimbursementDAO, never()).save(any());
    }

    @Test
    void resolve_rejectsAlreadyDenied() {
        Reimbursement original = pending();
        original.setStatus(Status.DENIED);
        when(reimbursementDAO.findById(10)).thenReturn(Optional.of(original));

        assertThrows(IllegalArgumentException.class,
                () -> service.resolveReimbursement(10, author(99), Status.APPROVED));
    }
}
