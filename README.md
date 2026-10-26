# Security Incident Management

Two-project scaffold for the ENSET Spring Security, JWT, and React assignment.

## Projects

- `backend/` — Spring Boot 3, Java 17, Spring Web, Security, Data JPA, Validation, H2, and JJWT dependencies. Default API port: 8080.
- `frontend/` — React, Vite, Axios, and React Router dependencies. Default development port: 5173.

## Database model

The backend now defines `User`, `Incident`, and `IncidentHistory` entities with JPA repositories and role/category/priority/status enums. Incident creator and optional technician assignment are separate lazy user relationships. `IncidentHistory` stores chronological status changes and treatment notes. See [docs/erd.md](docs/erd.md) for the ERD and relationship choices.

## Run

Backend (requires Maven):

```powershell
cd backend
mvn spring-boot:run
```

Frontend (requires Node.js):

```powershell
cd frontend
npm install
npm run dev
```

Authentication, authorization, domain APIs, and application screens are subsequent implementation steps.
