package com.enset.security.api.dto;
import com.enset.security.model.enums.IncidentCategory;
import com.enset.security.model.enums.IncidentPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record IncidentCreateRequest(@NotBlank @Size(max=180) String title, @NotBlank @Size(max=10000) String description, @NotNull IncidentCategory category, @NotNull IncidentPriority priority) {}
