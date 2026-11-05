package com.enset.security.api;

import com.enset.security.api.dto.*;
import com.enset.security.model.Incident;
import com.enset.security.model.IncidentHistory;
import com.enset.security.model.enums.IncidentStatus;
import com.enset.security.model.enums.Role;
import com.enset.security.repository.IncidentRepository;
import com.enset.security.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/incidents")
@Transactional(readOnly = true)
public class IncidentController {
    private final IncidentRepository incidents;
    private final UserRepository users;
    public IncidentController(IncidentRepository incidents, UserRepository users) { this.incidents = incidents; this.users = users; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public IncidentResponse create(Authentication auth, @Valid @RequestBody IncidentCreateRequest request) {
        var user = currentUser(auth);
        Incident incident = new Incident(request.title().trim(), request.description().trim(), request.category(), request.priority(), user);
        incident.addHistory(new IncidentHistory(user, null, IncidentStatus.OPEN, null, "Incident créé"));
        return response(incidents.save(incident));
    }

    @GetMapping("/my")
    public List<IncidentResponse> mine(Authentication auth) {
        return incidents.findByCreatedByIdOrderByCreatedAtDesc(currentUser(auth).getId()).stream().map(this::response).toList();
    }

    @GetMapping("/assigned")
    public List<IncidentResponse> assigned(Authentication auth) {
        requireRole(auth, Role.TECHNICIAN);
        return incidents.findByAssignedToIdOrderByUpdatedAtDesc(currentUser(auth).getId()).stream().map(this::response).toList();
    }

    @GetMapping
    public List<IncidentResponse> all(Authentication auth) {
        requireRole(auth, Role.ADMIN);
        return incidents.findAll().stream().map(this::response).toList();
    }

    @GetMapping("/{id}")
    public IncidentResponse get(Authentication auth, @PathVariable Long id) {
        Incident incident = find(id);
        if (hasRole(auth, Role.ADMIN) || incident.getCreatedBy().getEmail().equals(auth.getName())
                || (hasRole(auth, Role.TECHNICIAN) && incident.getAssignedTo() != null && incident.getAssignedTo().getEmail().equals(auth.getName())))
            return response(incident);
        throw new AccessDeniedException("You cannot access this incident");
    }

    @PutMapping("/{id}/status")
    @Transactional
    public IncidentResponse updateStatus(Authentication auth, @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        boolean admin = hasRole(auth, Role.ADMIN);
        if (!admin) requireRole(auth, Role.TECHNICIAN);
        Incident incident = find(id); var actor = currentUser(auth);
        if (!admin && (incident.getAssignedTo() == null || !incident.getAssignedTo().getId().equals(actor.getId())))
            throw new AccessDeniedException("Incident is not assigned to you");
        IncidentStatus old = incident.getStatus();
        if (old == IncidentStatus.CLOSED) throw new ResponseStatusException(HttpStatus.CONFLICT, "Closed incidents cannot be reopened through this endpoint");
        if (request.status() == IncidentStatus.CLOSED && !admin) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only an administrator may close an incident");
        if (!(admin && old == IncidentStatus.RESOLVED && request.status() == IncidentStatus.CLOSED) && !validTransition(old, request.status())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid status transition");
        incident.setStatus(request.status());
        incident.addHistory(new IncidentHistory(actor, old, request.status(), request.treatmentNote(), "Statut mis à jour"));
        return response(incidents.save(incident));
    }

    @PutMapping("/{id}/assign/{technicianId}")
    @Transactional
    public IncidentResponse assign(Authentication auth, @PathVariable Long id, @PathVariable Long technicianId) {
        requireRole(auth, Role.ADMIN);
        Incident incident = find(id);
        var technician = users.findById(technicianId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Technician not found"));
        if (technician.getRole() != Role.TECHNICIAN)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignee must have the TECHNICIAN role");
        if (!technician.isEnabled())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Disabled users cannot be assigned incidents");
        var previous = incident.getAssignedTo();
        IncidentStatus oldStatus = incident.getStatus();
        incident.setAssignedTo(technician);
        if (incident.getStatus() == IncidentStatus.OPEN) incident.setStatus(IncidentStatus.ASSIGNED);
        incident.addHistory(new IncidentHistory(currentUser(auth), oldStatus, incident.getStatus(), null,
            previous == null ? "Incident affecté à " + technician.getFullName() : "Incident réaffecté à " + technician.getFullName()));
        return response(incidents.save(incident));
    }

    private boolean validTransition(IncidentStatus old, IncidentStatus next) {
        return switch(old) {
            case OPEN -> next == IncidentStatus.IN_PROGRESS || next == IncidentStatus.RESOLVED;
            case ASSIGNED -> next == IncidentStatus.IN_PROGRESS || next == IncidentStatus.RESOLVED;
            case IN_PROGRESS -> next == IncidentStatus.RESOLVED;
            case RESOLVED -> next == IncidentStatus.IN_PROGRESS;
            case CLOSED -> false;
        };
    }
    private Incident find(Long id) { return incidents.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found")); }
    private com.enset.security.model.User currentUser(Authentication auth) { return users.findByEmail(auth.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")); }
    private void requireRole(Authentication auth, Role role) { if (!hasRole(auth, role)) throw new AccessDeniedException("Role " + role + " required"); }
    private boolean hasRole(Authentication auth, Role role) { return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + role.name())); }
    private IncidentResponse response(Incident i) {
        return new IncidentResponse(i.getId(), i.getTitle(), i.getDescription(), i.getCategory().name(), i.getPriority().name(), i.getStatus().name(), i.getCreatedAt(), i.getUpdatedAt(), i.getCreatedBy().getId(), i.getCreatedBy().getFullName(), i.getAssignedTo()==null?null:i.getAssignedTo().getId(), i.getAssignedTo()==null?null:i.getAssignedTo().getFullName(), i.getHistory().stream().map(h -> new HistoryResponse(h.getId(), h.getSummary(), h.getOldStatus()==null?null:h.getOldStatus().name(), h.getNewStatus()==null?null:h.getNewStatus().name(), h.getTreatmentNote(), h.getChangedBy().getFullName(), h.getCreatedAt())).toList());
    }
}
