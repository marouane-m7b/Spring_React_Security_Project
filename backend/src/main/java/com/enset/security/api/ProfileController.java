package com.enset.security.api;

import com.enset.security.api.dto.ProfileResponse;
import com.enset.security.api.dto.UpdateProfileRequest;
import com.enset.security.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final UserRepository users;
    public ProfileController(UserRepository users) { this.users = users; }

    @GetMapping
    public ProfileResponse get(Authentication auth) { return profile(auth.getName()); }

    @PutMapping
    public ProfileResponse update(Authentication auth, @Valid @RequestBody UpdateProfileRequest request) {
        var user = users.findByEmail(auth.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (users.findByEmail(email).filter(existing -> !existing.getId().equals(user.getId())).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        user.setFullName(request.fullName().trim()); user.setEmail(email);
        users.save(user);
        return new ProfileResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name(), user.isEnabled(), user.getCreatedAt());
    }

    private ProfileResponse profile(String email) {
        var user = users.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new ProfileResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name(), user.isEnabled(), user.getCreatedAt());
    }
}
