package com.enset.security.api.dto;
import java.time.Instant;
import java.util.List;
public record IncidentResponse(Long id, String title, String description, String category, String priority, String status, Instant createdAt, Instant updatedAt, Long createdById, String createdBy, Long assignedToId, String assignedTo, List<HistoryResponse> history) {}
