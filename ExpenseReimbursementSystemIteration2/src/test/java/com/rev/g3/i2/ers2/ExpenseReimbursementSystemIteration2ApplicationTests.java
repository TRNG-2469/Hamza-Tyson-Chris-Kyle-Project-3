package com.rev.g3.i2.ers2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Full application-context smoke test. Boots every bean (controllers, services, DAOs, security chain)
 * against the real PostgreSQL configured in application.properties / .env.
 *
 * It only runs when the environment variable {@code RUN_DB_TESTS=true} is set, so the normal
 * {@code mvn test} run stays green on a machine without a database. To run it:
 * {@code RUN_DB_TESTS=true mvn test -Dtest=ExpenseReimbursementSystemIteration2ApplicationTests}
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "RUN_DB_TESTS", matches = "true")
class ExpenseReimbursementSystemIteration2ApplicationTests {

    @Test
    void contextLoads() {
    }
}
