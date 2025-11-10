package com.claims.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class ClaimSubmissionRequest {
    @NotBlank(message = "Policy number is required")
    @Pattern(regexp = "^(POL\\d{8}|INS-\\d{9})$", message = "Invalid policy number format")
    private String policyNumber;
    
    @NotBlank(message = "Claim type is required")
    private String claimType;
    
    @NotBlank(message = "Nature of claim is required")
    private String natureOfClaim;
    
    @NotNull(message = "Date of incident is required")
    @PastOrPresent(message = "Incident date cannot be in the future")
    private LocalDate dateOfIncident;
    
    private LocalTime timeOfIncident;
    
    @NotBlank(message = "Location of incident is required")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Location must contain only alphabets")
    private String locationOfIncident;
    
    @NotBlank(message = "Description of incident is required")
    @Size(min = 15, max = 500, message = "Description must be between 15 and 500 characters")
    private String descriptionOfIncident;
    
    @NotNull(message = "Third party involvement must be specified")
    private Boolean wasThirdPartyInvolved;
    
    @NotNull(message = "Police report filing must be specified")
    private Boolean policeReportFiled;
    
    private String policeReportNumber;
    private String policeStationName;
    
    @NotNull(message = "Claimant policyholder status must be specified")
    private Boolean isClaimantPolicyholder;
    
    private String claimantName;
    private String relationship;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @DecimalMin(value = "0.01", message = "Claim amount must be positive")
    private BigDecimal claimAmount;
    
    private Boolean injuriesInvolved;
    private String injuryType;
    private String injurySeverity;
    
    @NotNull(message = "Declaration acceptance is required")
    private Boolean declarationAccepted;
    
    @NotBlank(message = "OTP is required")
    private String otp;
    
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    
    public String getClaimType() { return claimType; }
    public void setClaimType(String claimType) { this.claimType = claimType; }
    
    public String getNatureOfClaim() { return natureOfClaim; }
    public void setNatureOfClaim(String natureOfClaim) { this.natureOfClaim = natureOfClaim; }
    
    public LocalDate getDateOfIncident() { return dateOfIncident; }
    public void setDateOfIncident(LocalDate dateOfIncident) { this.dateOfIncident = dateOfIncident; }
    
    public LocalTime getTimeOfIncident() { return timeOfIncident; }
    public void setTimeOfIncident(LocalTime timeOfIncident) { this.timeOfIncident = timeOfIncident; }
    
    public String getLocationOfIncident() { return locationOfIncident; }
    public void setLocationOfIncident(String locationOfIncident) { this.locationOfIncident = locationOfIncident; }
    
    public String getDescriptionOfIncident() { return descriptionOfIncident; }
    public void setDescriptionOfIncident(String descriptionOfIncident) { this.descriptionOfIncident = descriptionOfIncident; }
    
    public Boolean getWasThirdPartyInvolved() { return wasThirdPartyInvolved; }
    public void setWasThirdPartyInvolved(Boolean wasThirdPartyInvolved) { this.wasThirdPartyInvolved = wasThirdPartyInvolved; }
    
    public Boolean getPoliceReportFiled() { return policeReportFiled; }
    public void setPoliceReportFiled(Boolean policeReportFiled) { this.policeReportFiled = policeReportFiled; }
    
    public String getPoliceReportNumber() { return policeReportNumber; }
    public void setPoliceReportNumber(String policeReportNumber) { this.policeReportNumber = policeReportNumber; }
    
    public String getPoliceStationName() { return policeStationName; }
    public void setPoliceStationName(String policeStationName) { this.policeStationName = policeStationName; }
    
    public Boolean getIsClaimantPolicyholder() { return isClaimantPolicyholder; }
    public void setIsClaimantPolicyholder(Boolean isClaimantPolicyholder) { this.isClaimantPolicyholder = isClaimantPolicyholder; }
    
    public String getClaimantName() { return claimantName; }
    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }
    
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public BigDecimal getClaimAmount() { return claimAmount; }
    public void setClaimAmount(BigDecimal claimAmount) { this.claimAmount = claimAmount; }
    
    public Boolean getInjuriesInvolved() { return injuriesInvolved; }
    public void setInjuriesInvolved(Boolean injuriesInvolved) { this.injuriesInvolved = injuriesInvolved; }
    
    public String getInjuryType() { return injuryType; }
    public void setInjuryType(String injuryType) { this.injuryType = injuryType; }
    
    public String getInjurySeverity() { return injurySeverity; }
    public void setInjurySeverity(String injurySeverity) { this.injurySeverity = injurySeverity; }
    
    public Boolean getDeclarationAccepted() { return declarationAccepted; }
    public void setDeclarationAccepted(Boolean declarationAccepted) { this.declarationAccepted = declarationAccepted; }
    
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}