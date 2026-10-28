package com.enset.security.api.dto;
import jakarta.validation.constraints.NotNull;
public record UpdateEnabledRequest(@NotNull Boolean enabled) {}
