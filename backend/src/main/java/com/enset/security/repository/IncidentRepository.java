package com.enset.security.repository;

import com.enset.security.model.Incident;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByCreatedByIdOrderByCreatedAtDesc(Long userId);
    List<Incident> findByAssignedToIdOrderByUpdatedAtDesc(Long technicianId);
    long countByStatus(com.enset.security.model.enums.IncidentStatus status);
}
