package com.enset.security.api.dto;
import com.enset.security.model.enums.Role;
import jakarta.validation.constraints.NotNull;
public record UpdateRoleRequest(@NotNull Role role) {}
