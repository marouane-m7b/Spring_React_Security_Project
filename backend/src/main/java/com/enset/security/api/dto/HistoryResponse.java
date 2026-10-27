package com.enset.security.api.dto;
import java.time.Instant;
public record HistoryResponse(Long id, String summary, String oldStatus, String newStatus, String treatmentNote, String changedBy, Instant createdAt) {}
