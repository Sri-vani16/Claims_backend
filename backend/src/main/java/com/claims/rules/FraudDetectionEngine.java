package com.claims.rules;

import com.claims.entity.Claim;
import com.claims.entity.FraudFlag;
import com.claims.entity.Policy;
import com.claims.repository.ClaimRepository;
import com.claims.repository.FraudFlagRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class FraudDetectionEngine {
    
    private static final Logger log = LoggerFactory.getLogger(FraudDetectionEngine.class);
    
    private final ClaimRepository claimRepository;
    private final FraudFlagRepository fraudFlagRepository;
    private final ObjectMapper objectMapper;
    
    public FraudDetectionEngine(ClaimRepository claimRepository, FraudFlagRepository fraudFlagRepository, ObjectMapper objectMapper) {
        this.claimRepository = claimRepository;
        this.fraudFlagRepository = fraudFlagRepository;
        this.objectMapper = objectMapper;
    }
    
    private List<FraudRule> fraudRules;
    
    public FraudDetectionResult detectFraud(Claim claim) {
        loadFraudRules();
        
        List<FraudFlag> detectedFlags = new ArrayList<>();
        int totalScore = 0;
        
        for (FraudRule rule : fraudRules) {
            if (!rule.isEnabled()) continue;
            
            boolean ruleTriggered = false;
            String reason = "";
            
            switch (rule.getRuleId()) {
                case "FD001":
                    ruleTriggered = checkDuplicateClaims(claim);
                    reason = "Duplicate claim detected for same policy and incident date";
                    break;
                case "FD002":
                    ruleTriggered = checkRecentlyInsuredVehicle(claim);
                    reason = "Vehicle insured recently before claim";
                    break;
                case "FD003":
                    ruleTriggered = checkUnusualClaimTime(claim);
                    reason = "Claim filed at unusual hours";
                    break;
                case "FD004":
                    ruleTriggered = checkFrequentClaims(claim);
                    reason = "Multiple claims from same policy in short period";
                    break;
                case "FD005":
                    ruleTriggered = checkDelayedFiling(claim);
                    reason = "Claim filed significantly after incident date";
                    break;
                case "FD006":
                    ruleTriggered = checkInvalidPoliceReport(claim);
                    reason = "Invalid or suspicious police report details";
                    break;
                case "FD007":
                    ruleTriggered = checkClaimAmountVsSumInsured(claim);
                    reason = "Claim amount suspicious compared to sum insured";
                    break;
                case "FD008":
                    ruleTriggered = checkIdentityMismatch(claim);
                    reason = "Identity mismatch detected";
                    break;
            }
            
            if (ruleTriggered) {
                FraudFlag flag = new FraudFlag();
                flag.setClaim(claim);
                flag.setRuleCode(rule.getRuleId());
                flag.setRuleName(rule.getRuleName());
                flag.setWeight(rule.getWeight());
                flag.setReason(reason);
                flag.setDetectedAt(LocalDateTime.now());
                
                detectedFlags.add(flag);
                totalScore += rule.getWeight();
                
                log.info("Fraud rule {} triggered for claim {}", rule.getRuleId(), claim.getClaimReferenceNumber());
            }
        }
        
        // Save fraud flags
        fraudFlagRepository.saveAll(detectedFlags);
        
        FraudDetectionResult result = new FraudDetectionResult();
        result.setFraudScore(totalScore);
        result.setFraudFlags(detectedFlags);
        return result;
    }
    
    private void loadFraudRules() {
        if (fraudRules == null) {
            try {
                ClassPathResource resource = new ClassPathResource("fraudRules.json");
                var rulesWrapper = objectMapper.readValue(resource.getInputStream(), 
                    new TypeReference<java.util.Map<String, List<FraudRule>>>() {});
                fraudRules = rulesWrapper.get("fraudDetectionRules");
                if (fraudRules == null) {
                    fraudRules = new ArrayList<>();
                }
            } catch (Exception e) {
                log.error("Failed to load fraud rules", e);
                fraudRules = new ArrayList<>();
            }
        }
    }
    
    private boolean checkDuplicateClaims(Claim claim) {
        List<Claim> duplicates = claimRepository.findDuplicateClaims(
            claim.getPolicy().getPolicyNumber(), 
            claim.getDateOfIncident()
        );
        return !duplicates.isEmpty();
    }
    
    private boolean checkRecentlyInsuredVehicle(Claim claim) {
        Policy policy = claim.getPolicy();
        if (policy.getStartDate() != null) {
            long daysBetween = ChronoUnit.DAYS.between(policy.getStartDate(), claim.getDateOfIncident());
            return daysBetween < 30; // Vehicle insured less than 30 days ago
        }
        return false;
    }
    
    private boolean checkUnusualClaimTime(Claim claim) {
        if (claim.getTimeOfIncident() != null) {
            LocalTime time = claim.getTimeOfIncident();
            return time.isBefore(LocalTime.of(6, 0)) || time.isAfter(LocalTime.of(22, 0));
        }
        return false;
    }
    
    private boolean checkFrequentClaims(Claim claim) {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        Long claimCount = claimRepository.countClaimsByPolicyInPeriod(
            claim.getPolicy().getPolicyNumber(), 
            threeMonthsAgo
        );
        return claimCount >= 3;
    }
    
    private boolean checkDelayedFiling(Claim claim) {
        long daysBetween = ChronoUnit.DAYS.between(claim.getDateOfIncident(), LocalDateTime.now().toLocalDate());
        return daysBetween > 30; // Filed more than 30 days after incident
    }
    
    private boolean checkInvalidPoliceReport(Claim claim) {
        if (claim.getPoliceReportFiled()) {
            return claim.getPoliceReportNumber() == null || 
                   claim.getPoliceReportNumber().trim().isEmpty() ||
                   claim.getPoliceStationName() == null ||
                   claim.getPoliceStationName().trim().isEmpty();
        }
        return false;
    }
    
    private boolean checkClaimAmountVsSumInsured(Claim claim) {
        if (claim.getClaimAmount() != null && claim.getPolicy().getSumInsured() != null) {
            double ratio = claim.getClaimAmount().divide(claim.getPolicy().getSumInsured(), java.math.RoundingMode.HALF_UP).doubleValue();
            return ratio > 0.8; // Claim amount is more than 80% of sum insured
        }
        return false;
    }
    
    private boolean checkIdentityMismatch(Claim claim) {
        if (!claim.getIsClaimantPolicyholder()) {
            List<Claim> similarClaims = claimRepository.findClaimsByEmailOrPhone(claim.getEmail(), claim.getPhone());
            return similarClaims.size() > 2; // Same contact details used in multiple claims
        }
        return false;
    }
}