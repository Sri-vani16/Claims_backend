package com.claims.dto;

import java.util.List;

public class ClaimSubmissionResponse {
    private String claimReferenceNumber;
    private Integer fraudScore;
    private List<String> fraudFlags;
    private String status;
    
    public ClaimSubmissionResponse() {}
    
    public ClaimSubmissionResponse(String claimReferenceNumber, Integer fraudScore, List<String> fraudFlags, String status) {
        this.claimReferenceNumber = claimReferenceNumber;
        this.fraudScore = fraudScore;
        this.fraudFlags = fraudFlags;
        this.status = status;
    }
    
    public String getClaimReferenceNumber() { return claimReferenceNumber; }
    public void setClaimReferenceNumber(String claimReferenceNumber) { this.claimReferenceNumber = claimReferenceNumber; }
    
    public Integer getFraudScore() { return fraudScore; }
    public void setFraudScore(Integer fraudScore) { this.fraudScore = fraudScore; }
    
    public List<String> getFraudFlags() { return fraudFlags; }
    public void setFraudFlags(List<String> fraudFlags) { this.fraudFlags = fraudFlags; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}