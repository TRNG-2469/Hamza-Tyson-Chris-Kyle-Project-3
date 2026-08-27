# Expense Reimbursement System — Iteration 2

Spring Boot 4 / Spring Security (JWT) / Spring Data JPA / PostgreSQL.

## Run

1. Create the database (default name `employee_db`) in PostgreSQL.
2. Edit `ExpenseReimbursementSystemIteration2/.env`:
   ```
   DB_URL=jdbc:postgresql://localhost:5432/employee_db
   DB_USER=postgres
   DB_PASS=postgres
   JWT_SECRET=<base64 string, at least 32 bytes>
   ```
   `App.main` loads this file, so no extra IDE environment setup is needed. Tables are created/updated
   automatically (`spring.jpa.hibernate.ddl-auto=update`). Insert at least one row into `Departments`
   before registering users.
3. `mvn spring-boot:run` (or run `App` from IntelliJ) and open <http://localhost:8080/>.

## Tests

`mvn test` runs the unit and web-slice tests without a database.
The full-context smoke test only runs when `RUN_DB_TESTS=true` is set in the environment.

## API

| Method | Path | Auth | Notes |
|---|---|---|---|
| POST | `/register` (or `/api/register`) | none | body: username, password, firstName, lastName, departmentId |
| POST | `/login` (or `/api/login`) | none | body: username, password → `{token, username, role}` |
| GET | `/user` | Bearer | current user |
| GET | `/departments`, `/departments/{id}` | none | |
| POST | `/reimbursements` | Bearer | body: amount, description, type (TRAVEL/FOOD/LODGING/OTHER) |
| GET | `/reimbursements/{authorId}?status=` | Bearer | |
| PATCH | `/reimbursements/{id}` | Bearer | edit a still-pending reimbursement |
| GET | `/manager/reimbursements?status=&departmentId=` | MANAGER | |
| PATCH | `/manager/reimbursements/{id}` | MANAGER | body: `{"status": "APPROVED"}` or `"DENIED"` |

Send the token as `Authorization: Bearer <token>` on protected requests.
