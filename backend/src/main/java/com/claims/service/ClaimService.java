package com.claims.service;

import com.claims.dto.ClaimSubmissionRequest;
import com.claims.dto.ClaimSubmissionResponse;
import com.claims.entity.Claim;
import com.claims.entity.Policy;
import com.claims.repository.ClaimRepository;
import com.claims.repository.PolicyRepository;
import com.claims.rules.FraudDetectionEngine;
import com.claims.rules.FraudDetectionResult;
import com.claims.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClaimService {
    
    private static final Logger log = LoggerFactory.getLogger(ClaimService.class);
    
    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;
    private final FraudDetectionEngine fraudDetectionEngine;
    private final EmailService emailService;
    
    public ClaimService(ClaimRepository claimRepository, PolicyRepository policyRepository, 
                       FraudDetectionEngine fraudDetectionEngine, EmailService emailService) {
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
        this.fraudDetectionEngine = fraudDetectionEngine;
        this.emailService = emailService;
    }
    
    @Transactional
    public ClaimSubmissionResponse submitClaim(ClaimSubmissionRequest request) {
        // Validate policy
        Policy policy = policyRepository.findByPolicyNumber(request.getPolicyNumber())
            .orElseThrow(() -> new RuntimeException("Policy not found"));
        
        // Validate policy is active and covers incident date
        if (!"ACTIVE".equals(policy.getStatus())) {
            throw new RuntimeException("Policy is not active");
        }
        
        if (request.getDateOfIncident().isBefore(policy.getStartDate()) || 
            request.getDateOfIncident().isAfter(policy.getEndDate())) {
            throw new RuntimeException("Incident date is not within policy coverage period");
        }
        
        // Create claim
        Claim claim = new Claim();
        claim.setClaimReferenceNumber(generateClaimReferenceNumber());
        claim.setPolicy(policy);
        claim.setClaimType(request.getClaimType());
        claim.setNatureOfClaim(request.getNatureOfClaim());
        claim.setDateOfIncident(request.getDateOfIncident());
        claim.setTimeOfIncident(request.getTimeOfIncident());
        claim.setLocationOfIncident(request.getLocationOfIncident());
        claim.setDescriptionOfIncident(request.getDescriptionOfIncident());
        claim.setWasThirdPartyInvolved(request.getWasThirdPartyInvolved());
        claim.setPoliceReportFiled(request.getPoliceReportFiled());
        claim.setPoliceReportNumber(request.getPoliceReportNumber());
        claim.setPoliceStationName(request.getPoliceStationName());
        claim.setIsClaimantPolicyholder(request.getIsClaimantPolicyholder());
        claim.setClaimantName(request.getClaimantName());
        claim.setRelationship(request.getRelationship());
        claim.setPhone(request.getPhone());
        claim.setEmail(request.getEmail());
        claim.setClaimAmount(request.getClaimAmount());
        claim.setInjuriesInvolved(request.getInjuriesInvolved());
        claim.setInjuryType(request.getInjuryType());
        claim.setInjurySeverity(request.getInjurySeverity());
        claim.setSubmissionDate(LocalDateTime.now());
        claim.setStatus("SUBMITTED");
        
        // Save claim first
        claim = claimRepository.save(claim);
        
        // Run fraud detection
        FraudDetectionResult fraudResult = fraudDetectionEngine.detectFraud(claim);
        
        // Update claim with fraud score
        claim.setFraudScore(fraudResult.getFraudScore());
        claim.setFraudFlags(fraudResult.getFraudFlags().stream()
            .map(flag -> flag.getRuleCode() + ": " + flag.getReason())
            .collect(Collectors.joining("; ")));
        
        claim = claimRepository.save(claim);
        
        // Send email alert if high risk
        if (fraudResult.getFraudScore() > 75) {
            emailService.sendFraudAlert(claim, fraudResult.getFraudFlags());
            log.info("High risk claim detected (score: {}), sending email alert", fraudResult.getFraudScore());
        }
        
        List<String> fraudFlags = fraudResult.getFraudFlags().stream()
            .map(flag -> flag.getRuleCode() + ": " + flag.getRuleName())
            .collect(Collectors.toList());
        
        return new ClaimSubmissionResponse(
            claim.getClaimReferenceNumber(),
            fraudResult.getFraudScore(),
            fraudFlags,
            claim.getStatus()
        );
    }
    
    private String generateClaimReferenceNumber() {
        return "CLM" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}