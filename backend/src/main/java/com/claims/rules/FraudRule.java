package com.claims.rules;

public class FraudRule {
    private String ruleId;
    private String ruleName;
    private String description;
    private String severity;
    private int weight;
    private String action;
    private boolean enabled;
    
    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    // Backward compatibility
    public String getCode() { return ruleId; }
    public String getName() { return ruleName; }
}