package com.enset.security.api.dto;
import com.enset.security.model.enums.IncidentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record StatusUpdateRequest(@NotNull IncidentStatus status, @Size(max=2000) String treatmentNote) {}
