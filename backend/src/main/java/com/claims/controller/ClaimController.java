package com.claims.controller;

import com.claims.dto.ClaimSubmissionRequest;
import com.claims.dto.ClaimSubmissionResponse;
import com.claims.service.ClaimService;
import com.claims.email.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Claims", description = "Claims management API")
public class ClaimController {
    
    private final ClaimService claimService;
    private final EmailService emailService;
    
    public ClaimController(ClaimService claimService, EmailService emailService) {
        this.claimService = claimService;
        this.emailService = emailService;
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Claims API is working!");
    }
    
    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail() {
        emailService.sendTestEmail();
        return ResponseEntity.ok("Test email sent! Check your inbox.");
    }
    
    @PostMapping("/submit")
    @Operation(summary = "Submit a new claim", description = "Submit a new insurance claim with fraud detection")
    public ResponseEntity<ClaimSubmissionResponse> submitClaim(@Valid @RequestBody ClaimSubmissionRequest request) {
        ClaimSubmissionResponse response = claimService.submitClaim(request);
        return ResponseEntity.ok(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        
        errors.put("timestamp", System.currentTimeMillis());
        errors.put("status", 400);
        errors.put("error", "Validation Failed");
        errors.put("fieldErrors", fieldErrors);
        errors.put("path", "/api/claims/submit");
        
        return ResponseEntity.badRequest().body(errors);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralError(Exception ex) {
        Map<String, Object> errors = new HashMap<>();
        
        errors.put("timestamp", System.currentTimeMillis());
        errors.put("status", 500);
        errors.put("error", "Internal Server Error");
        errors.put("message", ex.getMessage());
        errors.put("exception", ex.getClass().getSimpleName());
        errors.put("path", "/api/claims/submit");
        
        ex.printStackTrace(); // Print to console for debugging
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }
}