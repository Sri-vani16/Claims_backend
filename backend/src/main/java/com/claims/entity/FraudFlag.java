package com.claims.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_flags")
public class FraudFlag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;
    
    @Column(nullable = false)
    private String ruleCode;
    
    @Column(nullable = false)
    private String ruleName;
    
    @Column(nullable = false)
    private Integer weight;
    
    @Column(nullable = false)
    private String reason;
    
    @Column(nullable = false)
    private LocalDateTime detectedAt;
    
    public FraudFlag() {}
    
    public FraudFlag(Long id, Claim claim, String ruleCode, String ruleName, Integer weight, String reason, LocalDateTime detectedAt) {
        this.id = id;
        this.claim = claim;
        this.ruleCode = ruleCode;
        this.ruleName = ruleName;
        this.weight = weight;
        this.reason = reason;
        this.detectedAt = detectedAt;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Claim getClaim() { return claim; }
    public void setClaim(Claim claim) { this.claim = claim; }
    
    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
    
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    
    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
}