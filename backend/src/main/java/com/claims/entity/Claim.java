package com.claims.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "claims")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String claimReferenceNumber;
    
    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;
    
    @Column(nullable = false)
    private String claimType;
    
    @Column(nullable = false)
    private String natureOfClaim;
    
    @Column(nullable = false)
    private LocalDate dateOfIncident;
    
    private LocalTime timeOfIncident;
    
    @Column(nullable = false)
    private String locationOfIncident;
    
    @Column(nullable = false, length = 500)
    private String descriptionOfIncident;
    
    @Column(nullable = false)
    private Boolean wasThirdPartyInvolved;
    
    @Column(nullable = false)
    private Boolean policeReportFiled;
    
    private String policeReportNumber;
    private String policeStationName;
    
    @Column(nullable = false)
    private Boolean isClaimantPolicyholder;
    
    private String claimantName;
    private String relationship;
    private String phone;
    private String email;
    
    private BigDecimal claimAmount;
    
    @Column(nullable = false)
    private LocalDateTime submissionDate;
    
    @Column(nullable = false)
    private String status;
    
    private Integer fraudScore;
    
    @Column(length = 1000)
    private String fraudFlags;
    
    private Boolean injuriesInvolved;
    private String injuryType;
    private String injurySeverity;
    
    public Claim() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getClaimReferenceNumber() { return claimReferenceNumber; }
    public void setClaimReferenceNumber(String claimReferenceNumber) { this.claimReferenceNumber = claimReferenceNumber; }
    
    public Policy getPolicy() { return policy; }
    public void setPolicy(Policy policy) { this.policy = policy; }
    
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
    
    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getFraudScore() { return fraudScore; }
    public void setFraudScore(Integer fraudScore) { this.fraudScore = fraudScore; }
    
    public String getFraudFlags() { return fraudFlags; }
    public void setFraudFlags(String fraudFlags) { this.fraudFlags = fraudFlags; }
    
    public Boolean getInjuriesInvolved() { return injuriesInvolved; }
    public void setInjuriesInvolved(Boolean injuriesInvolved) { this.injuriesInvolved = injuriesInvolved; }
    
    public String getInjuryType() { return injuryType; }
    public void setInjuryType(String injuryType) { this.injuryType = injuryType; }
    
    public String getInjurySeverity() { return injurySeverity; }
    public void setInjurySeverity(String injurySeverity) { this.injurySeverity = injurySeverity; }
}