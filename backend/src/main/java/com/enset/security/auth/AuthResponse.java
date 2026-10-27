package com.enset.security.auth;

import java.time.Instant;

public record AuthResponse(
    String token,
    String tokenType,
    String email,
    String fullName,
    String role,
    Instant issuedAt,
    Instant expiresAt
) {}
