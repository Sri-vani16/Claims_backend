package com.claims.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "System", description = "System information and health endpoints")
public class HomeController {
    
    @GetMapping("/api")
    @Operation(summary = "API Information", description = "Get system information and available endpoints")
    @ApiResponse(responseCode = "200", description = "System information retrieved")
    public Map<String, Object> home() {
        return Map.of(
            "message", "Claims Fraud Detection System API",
            "status", "Running",
            "version", "1.0.0",
            "endpoints", Map.of(
                "swagger", "/swagger-ui.html",
                "api-docs", "/api-docs",
                "claims-api", "/api/claims",
                "monitoring-api", "/api/monitoring",
                "health", "/health"
            ),
            "swagger-url", "https://your-app-name.onrender.com/swagger-ui.html"
        );
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Check if the API is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
    
    @GetMapping("/")
    public String root() {
        return "redirect:/swagger-ui.html";
    }
}