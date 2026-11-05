package com.enset.security.api;

import com.enset.security.api.dto.*;
import com.enset.security.model.enums.IncidentStatus;
import com.enset.security.model.enums.Role;
import com.enset.security.repository.IncidentRepository;
import com.enset.security.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository users;
    private final IncidentRepository incidents;

    public AdminController(UserRepository users, IncidentRepository incidents) {
        this.users = users;
        this.incidents = incidents;
    }

    @GetMapping("/users")
    public List<AdminUserResponse> users(Authentication auth) {
        requireAdmin(auth);
        return users.findAll().stream().map(this::userResponse).toList();
    }

    @PutMapping("/users/{id}/role")
    public AdminUserResponse updateRole(Authentication auth, @PathVariable Long id,
                                        @Valid @RequestBody UpdateRoleRequest request) {
        requireAdmin(auth);
        var user = findUser(id);
        if (user.getId().equals(findCurrentUserId(auth)) && request.role() != Role.ADMIN)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You cannot remove your own administrator role");
        user.setRole(request.role());
        return userResponse(users.save(user));
    }

    @PutMapping("/users/{id}/enabled")
    public AdminUserResponse updateEnabled(Authentication auth, @PathVariable Long id,
                                           @Valid @RequestBody UpdateEnabledRequest request) {
        requireAdmin(auth);
        var user = findUser(id);
        if (user.getId().equals(findCurrentUserId(auth)) && !request.enabled())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You cannot disable your own account");
        user.setEnabled(request.enabled());
        return userResponse(users.save(user));
    }

    @PutMapping("/users/{id}/enable")
    public AdminUserResponse enable(Authentication auth, @PathVariable Long id) {
        return updateEnabled(auth, id, new UpdateEnabledRequest(true));
    }

    @PutMapping("/users/{id}/disable")
    public AdminUserResponse disable(Authentication auth, @PathVariable Long id) {
        return updateEnabled(auth, id, new UpdateEnabledRequest(false));
    }

    @GetMapping("/statistics")
    public StatisticsResponse statistics(Authentication auth) {
        requireAdmin(auth);
        MapBuilder counts = new MapBuilder();
        for (IncidentStatus status : IncidentStatus.values())
            counts.add(status.name(), incidents.countByStatus(status));
        return new StatisticsResponse(users.count(), incidents.count(), counts.build());
    }

    private com.enset.security.model.User findUser(Long id) {
        return users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
    private Long findCurrentUserId(Authentication auth) {
        return users.findByEmail(auth.getName()).map(com.enset.security.model.User::getId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
    private void requireAdmin(Authentication auth) {
        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            throw new AccessDeniedException("Administrator role required");
    }
    private AdminUserResponse userResponse(com.enset.security.model.User user) {
        return new AdminUserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name(), user.isEnabled(), user.getCreatedAt());
    }
    private static final class MapBuilder {
        private final java.util.Map<String, Long> values = new LinkedHashMap<>();
        void add(String key, long value) { values.put(key, value); }
        java.util.Map<String, Long> build() { return java.util.Map.copyOf(values); }
    }
}
