# Demy API

[English](./README.md) | [Español](./README.es.md)

![Java 21](https://img.shields.io/badge/Java_21-ED8B00?logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?logo=springboot&logoColor=white) ![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white)

The REST API that powers **Demy**, a multiplatform academy-management product built by [Nistra](https://github.com/nistrahq). It centralizes identity, institutions, enrollment, scheduling, attendance, billing, and accounting so the administrator, teacher, and student applications share one source of truth.

## What it provides

- JWT-based authentication, account recovery, and role-aware access.
- Academy, teacher, student, course, classroom, and academic-period management.
- Enrollment and schedule workflows for administrators, teachers, and students.
- Attendance recording and reporting.
- Billing, transactions, financial summaries, and PDF/Excel report endpoints.
- English and Spanish message bundles.
- OpenAPI 3 documentation through Swagger UI.

## Architecture

The service is built with **Java 21**, **Spring Boot**, **Spring Data JPA**, and **MySQL**. Its package structure follows Domain-Driven Design with these bounded contexts:

```text
com.nistra.demy.platform
├── iam
├── institution
├── enrollment
├── scheduling
├── attendance
├── billing
├── accountingfinance
└── shared
```

Each bounded context separates application, domain, infrastructure, and interface concerns. `shared` contains cross-cutting infrastructure rather than business ownership.

## API preview

![Demy API overview in Swagger UI](./docs/screenshots/swagger-overview.png)

<details>
<summary>Browse more API screenshots</summary>

### Endpoint catalog

![Demy API endpoint catalog](./docs/screenshots/swagger-endpoints.png)

### Student schedule request

![Student schedule request in Swagger UI](./docs/screenshots/swagger-schedule-by-student.png)

</details>

## Run locally

### Requirements

- JDK 21
- MySQL 8+
- The included Maven Wrapper

Configure the variables referenced by `application.properties` and `application-dev.properties`, including the database connection, JWT settings, documentation contact, and mail sender. Then run:

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

The default local endpoints are:

- API: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI document: `http://localhost:8080/v3/api-docs`

Swagger advertises a relative server URL, so requests stay on the host that served the documentation.

## Verification

```bash
./mvnw test
./mvnw package
```

Integration tests require a valid MySQL test configuration.

## Demy ecosystem

- [Landing page](https://github.com/nistrahq/demy-landing)
- [Administrator app](https://github.com/nistrahq/demy-admins)
- [Teacher app](https://github.com/nistrahq/demy-teachers)
- [Student app](https://github.com/nistrahq/demy-students)

See [CONTRIBUTING.md](./CONTRIBUTING.md) for the Git workflow and coding conventions.
