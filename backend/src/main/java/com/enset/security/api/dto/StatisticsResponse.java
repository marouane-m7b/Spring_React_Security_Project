package com.enset.security.api.dto;
import java.util.Map;
public record StatisticsResponse(long totalUsers, long totalIncidents, Map<String, Long> incidentsByStatus) {}
