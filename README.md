# Security Incident Management

Two-project scaffold for the ENSET Spring Security, JWT, and React assignment.

## Projects

- `backend/` — Spring Boot 3, Java 17, Spring Web, Security, Data JPA, Validation, H2, and JJWT dependencies. Default API port: 8080.
- `frontend/` — React, Vite, Axios, and React Router dependencies. Default development port: 5173.

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

This is the initialization scaffold only. Authentication, authorization, domain APIs, and application screens are not implemented yet.
