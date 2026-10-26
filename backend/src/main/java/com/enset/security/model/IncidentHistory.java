package com.enset.security.model;

import com.enset.security.model.enums.IncidentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

@Entity
@Table(name = "incident_history", indexes = @Index(name = "idx_history_incident_created", columnList = "incident_id, created_at"))
public class IncidentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false, foreignKey = @ForeignKey(name = "fk_history_incident"))
    private Incident incident;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "changed_by_id", nullable = false, foreignKey = @ForeignKey(name = "fk_history_actor"))
    private User changedBy;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private IncidentStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private IncidentStatus newStatus;

    @Size(max = 2000)
    @Column(length = 2000)
    private String treatmentNote;

    @NotBlank
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String summary;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected IncidentHistory() {}

    public IncidentHistory(User changedBy, IncidentStatus oldStatus, IncidentStatus newStatus,
                           String treatmentNote, String summary) {
        this.changedBy = changedBy;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.treatmentNote = treatmentNote;
        this.summary = summary;
    }

    @PrePersist
    void onCreate() { if (createdAt == null) createdAt = Instant.now(); }

    public Long getId() { return id; }
    public Incident getIncident() { return incident; }
    void setIncident(Incident incident) { this.incident = incident; }
    public User getChangedBy() { return changedBy; }
    public IncidentStatus getOldStatus() { return oldStatus; }
    public IncidentStatus getNewStatus() { return newStatus; }
    public String getTreatmentNote() { return treatmentNote; }
    public String getSummary() { return summary; }
    public Instant getCreatedAt() { return createdAt; }
}
