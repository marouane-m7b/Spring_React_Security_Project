# ERD — Security Incident Management

```mermaid
erDiagram
    USER ||--o{ INCIDENT : "creates (created_by)"
    USER o|--o{ INCIDENT : "is assigned (assigned_to)"
    INCIDENT ||--o{ INCIDENT_HISTORY : "has timeline"
    USER ||--o{ INCIDENT_HISTORY : "performs change (changed_by)"

    USER {
        bigint id PK
        varchar full_name
        varchar email UK
        varchar password "BCrypt hash"
        enum role "EMPLOYEE, TECHNICIAN, ADMIN"
        boolean enabled
        timestamp created_at
    }

    INCIDENT {
        bigint id PK
        varchar title
        varchar description
        enum category
        enum priority
        enum status
        timestamp created_at
        timestamp updated_at
        bigint created_by_id FK
        bigint assigned_to_id FK "nullable"
    }

    INCIDENT_HISTORY {
        bigint id PK
        bigint incident_id FK
        bigint changed_by_id FK
        enum old_status "nullable"
        enum new_status "nullable"
        varchar treatment_note "nullable"
        varchar summary
        timestamp created_at
    }
```

## Relationship and loading choices

- Each incident has exactly one creator and optionally one assigned technician. Both are `@ManyToOne(fetch = LAZY)`; the nullable assignment permits an unassigned incident.
- A user can create and be assigned many incidents (`@OneToMany`, lazy collections). The two user relationships use distinct foreign keys.
- Each timeline entry belongs to one incident and records the user who performed the action. Status fields are nullable so the same timeline can also represent assignment or a treatment note without a status transition.
- `EnumType.STRING` keeps roles, categories, priorities, and statuses readable and stable in the database. History entries are ordered by creation time.
- Lazy loading avoids fetching whole user/incident graphs for routine queries; API response DTOs should explicitly select the fields to expose and prevent recursive entity serialization.
