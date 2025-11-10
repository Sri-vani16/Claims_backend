package com.claims.rules;

import com.claims.entity.FraudFlag;

import java.util.List;

public class FraudDetectionResult {
    private int fraudScore;
    private List<FraudFlag> fraudFlags;
    
    public FraudDetectionResult() {}
    
    public FraudDetectionResult(int fraudScore, List<FraudFlag> fraudFlags) {
        this.fraudScore = fraudScore;
        this.fraudFlags = fraudFlags;
    }
    
    public int getFraudScore() { return fraudScore; }
    public void setFraudScore(int fraudScore) { this.fraudScore = fraudScore; }
    
    public List<FraudFlag> getFraudFlags() { return fraudFlags; }
    public void setFraudFlags(List<FraudFlag> fraudFlags) { this.fraudFlags = fraudFlags; }
}