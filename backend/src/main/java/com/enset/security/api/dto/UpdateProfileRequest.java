package com.enset.security.api.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record UpdateProfileRequest(@NotBlank @Size(max=120) String fullName, @NotBlank @Email @Size(max=254) String email) {}
