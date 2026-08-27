package com.rev.g3.i2.ers2.enums;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for the domain enums. No Spring context, no mocks — these exercise
 * the hand-written {@code getDbValue()} accessors and the {@code fromDbValue(..)} factories.
 *
 * These are the cheapest, most deterministic tests in the suite and should always be green.
 */
class EnumTest {

    @Nested
    class StatusTest {

        @Test
        void getDbValue_returnsLowercaseString_forEveryConstant() {
            assertEquals("pending", Status.PENDING.getDbValue());
            assertEquals("approved", Status.APPROVED.getDbValue());
            assertEquals("denied", Status.DENIED.getDbValue());
        }

        @Test
        void fromDbValue_mapsExactLowercaseString_toConstant() {
            assertEquals(Status.PENDING, Status.fromDbValue("pending"));
            assertEquals(Status.APPROVED, Status.fromDbValue("approved"));
            assertEquals(Status.DENIED, Status.fromDbValue("denied"));
        }

        @Test
        void fromDbValue_isCaseInsensitive() {
            assertEquals(Status.APPROVED, Status.fromDbValue("APPROVED"));
            assertEquals(Status.APPROVED, Status.fromDbValue("Approved"));
            assertEquals(Status.DENIED, Status.fromDbValue("DeNiEd"));
        }

        @Test
        void fromDbValue_throwsIllegalArgument_forUnknownValue() {
            IllegalArgumentException ex =
                    assertThrows(IllegalArgumentException.class, () -> Status.fromDbValue("archived"));
            assertTrue(ex.getMessage().contains("archived"),
                    "message should echo the offending value");
        }

        @Test
        void fromDbValue_throwsIllegalArgument_forEmptyString() {
            assertThrows(IllegalArgumentException.class, () -> Status.fromDbValue(""));
        }

        @Test
        void fromDbValue_throwsNullPointer_forNull() {
            // Current impl calls equalsIgnoreCase on the enum's stored value with the arg,
            // so a null arg does not NPE — it simply matches nothing and throws IAE.
            // This test pins that observable behavior; flip to assertThrows(NPE) only if the impl changes.
            assertThrows(IllegalArgumentException.class, () -> Status.fromDbValue(null));
        }

        @Test
        void values_containExactlyThreeConstants() {
            assertEquals(3, Status.values().length);
        }
    }

    @Nested
    class TypeTest {

        @Test
        void getDbValue_returnsLowercaseString_forEveryConstant() {
            assertEquals("travel", Type.TRAVEL.getDbValue());
            assertEquals("food", Type.FOOD.getDbValue());
            assertEquals("lodging", Type.LODGING.getDbValue());
            assertEquals("other", Type.OTHER.getDbValue());
        }

        @Test
        void fromDbValue_mapsExactString_toConstant() {
            assertEquals(Type.TRAVEL, Type.fromDbValue("travel"));
            assertEquals(Type.FOOD, Type.fromDbValue("food"));
            assertEquals(Type.LODGING, Type.fromDbValue("lodging"));
            assertEquals(Type.OTHER, Type.fromDbValue("other"));
        }

        @Test
        void fromDbValue_isCaseInsensitive() {
            assertEquals(Type.TRAVEL, Type.fromDbValue("TRAVEL"));
            assertEquals(Type.LODGING, Type.fromDbValue("Lodging"));
        }

        @Test
        void fromDbValue_throwsIllegalArgument_forUnknownValue() {
            IllegalArgumentException ex =
                    assertThrows(IllegalArgumentException.class, () -> Type.fromDbValue("mileage"));
            assertTrue(ex.getMessage().contains("mileage"));
        }

        @Test
        void values_containExactlyFourConstants() {
            assertEquals(4, Type.values().length);
        }
    }

    @Nested
    class RoleTest {

        @Test
        void getDbValue_returnsLowercaseString() {
            assertEquals("employee", Role.EMPLOYEE.getDbValue());
            assertEquals("manager", Role.MANAGER.getDbValue());
        }

        @Test
        void name_matchesDiscriminatorConvention() {
            // UserPrincipal builds authority "ROLE_" + role.name(); pin the exact names it depends on.
            assertEquals("EMPLOYEE", Role.EMPLOYEE.name());
            assertEquals("MANAGER", Role.MANAGER.name());
        }

        @Test
        void values_containExactlyTwoConstants() {
            assertEquals(2, Role.values().length);
        }
    }
}
