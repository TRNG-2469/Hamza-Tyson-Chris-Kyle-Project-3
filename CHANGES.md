# What was fixed

## Compile / startup blockers
- **User, Reimbursement**: `@ManyToOne @JoinColumn` on `int` fields (`departmentId`, `authorId`, `resolverId`)
  made Hibernate fail with *"targets the type 'int' which is not an @Entity"*. They are now plain
  `@Column` foreign-key columns. Also `@NotBlank` on enum fields (`role`, `type`, `status`) was replaced
  with `@NotNull` (`@NotBlank` only works on strings and throws at validation time).
- **UserDAO**: removed `User register(User)`. Spring Data tried to derive a query from the method name
  and failed at startup. The service already uses `save()`.
- **ReimbursementDAO**: JPQL referenced `r.author.department` / `r.author.userId`, which don't exist.
  Queries now use `r.authorId` and a sub-select on `User.departmentId`.
- **JwtService**: no longer calls `Dotenv` in its constructor (which crashed without a `.env` /
  `JWT_SECRET`). It reads `jwt.secret` from `application.properties` and falls back to a random key
  with a warning. `isTokenValid` now returns `false` for expired/bad tokens instead of throwing.
- **App.main** loads `.env` into system properties so `${DB_USER}` etc. resolve. `application.properties`
  has sensible defaults; `.env` now contains a generated `JWT_SECRET` and `DB_URL`.
- **AppConfig** removed — `@ComponentScan("...ers2.*")` was redundant with `@SpringBootApplication`.
- **pom.xml**: dropped the deprecated `spring-boot-starter-web` alias; replaced Kotlin-based
  `java-dotenv` (whose stdlib had been excluded → runtime `NoClassDefFoundError`) with pure-Java
  `dotenv-java` (same package, same API).
- **Enums**: added JPA `AttributeConverter`s so `Role/Status/Type` are stored as their lowercase
  `dbValue` ("pending", "manager", …) as the enums intended. Added `Role.fromDbValue`.

## Security / routing
- `SecurityConfig` only permitted `/api/register` and `/api/login`, but the controller and the HTML pages
  use `/register` and `/login`; there was no login endpoint at all. Now:
  - `POST /register`, `POST /login` (plus the `/api/...` aliases) are public; `/login` returns
    `AuthResponse {token, username, role}`.
  - `GET /user` returns the current user (password never serialized — `@JsonProperty(WRITE_ONLY)`).
  - `GET /departments` is public (the registration page needs it before an account exists).
  - `/manager/**` requires `ROLE_MANAGER`. Unauthenticated → 401, wrong role → 403.
- New accounts default to `Role.EMPLOYEE`.

## Controllers
- `ReimbursementHandler`: `@Param` (Spring Data) on query params replaced with `@RequestParam`;
  `PATCH /manager/reimbursements/{id}` now actually calls `resolveReimbursement` with the acting manager;
  status filter accepts `APPROVED` or `approved`.
- `GlobalExceptionHandler`: malformed JSON → 400 and unknown path → 404 (both used to fall into the
  500 catch-all).

## Front end
- Pages moved from `resources/pages` (not served) to `resources/static` so `/`, `/login.html`,
  `/register.html`, `/dashboard.html` work. `dashboard.html` now sends the saved JWT as a
  `Authorization: Bearer` header on every API call (`authFetch`).

## Tests
- Controller slice tests rewritten for Spring Boot 4 (`org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`),
  import the real `SecurityConfig` + `JwtAuthFilter` (with `JwtService`/`UserDetailsService` mocked),
  use JSON string bodies instead of autowiring Jackson 2's `ObjectMapper` (Boot 4 ships Jackson 3),
  and cover login, `/user`, role enforcement and the resolve endpoint.
- The `@SpringBootTest` smoke test runs only when `RUN_DB_TESTS=true` (needs a real Postgres).
