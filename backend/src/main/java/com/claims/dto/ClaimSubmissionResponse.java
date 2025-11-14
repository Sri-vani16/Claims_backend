package com.claims.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response object for claim submission")
public class ClaimSubmissionResponse {
    @Schema(description = "Unique claim reference number", example = "CLM-2024-001234")
    private String claimReferenceNumber;
    
    @Schema(description = "Fraud detection score (0-100)", example = "25")
    private Integer fraudScore;
    
    @Schema(description = "List of fraud flags detected", example = "[\"High claim amount\", \"Recent policy\"]")
    private List<String> fraudFlags;
    
    @Schema(description = "Claim processing status", example = "APPROVED")
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