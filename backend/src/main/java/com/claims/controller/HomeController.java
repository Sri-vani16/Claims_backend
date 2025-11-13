package com.claims.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {
    
    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
            "message", "Claims Fraud Detection System API",
            "status", "Running",
            "version", "1.0.0",
            "endpoints", Map.of(
                "swagger", "/swagger-ui.html",
                "api", "/api/claims",
                "monitoring", "/api/monitoring",
                "health", "/actuator/health"
            )
        );
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}