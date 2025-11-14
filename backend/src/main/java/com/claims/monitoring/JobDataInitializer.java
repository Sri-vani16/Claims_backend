package com.claims.monitoring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class JobDataInitializer implements CommandLineRunner {
    
    private final JobMonitoringService jobMonitoringService;
    
    public JobDataInitializer(JobMonitoringService jobMonitoringService) {
        this.jobMonitoringService = jobMonitoringService;
    }
    
    @Override
    public void run(String... args) {
        try {
            createSampleJobs();
        } catch (Exception e) {
            // Sample data creation failed - not critical for application startup
            System.out.println("Sample job data initialization failed: " + e.getMessage());
        }
    }
    
    private void createSampleJobs() {
        // Create some failed jobs that will generate log entries
        String job1 = jobMonitoringService.startJob("CLAIM_PROCESSING", "Processing claim data");
        jobMonitoringService.failJob(job1, "Database connection timeout", 
            new RuntimeException("Connection timeout after 30 seconds"));
        
        String job2 = jobMonitoringService.startJob("FRAUD_DETECTION", "Running fraud detection");
        jobMonitoringService.failJob(job2, "Rule engine compilation error", 
            new IllegalArgumentException("Invalid regex pattern in rule FR-2024-001"));
        
        String job3 = jobMonitoringService.startJob("PAYMENT_PROCESSING", "Processing payment");
        jobMonitoringService.failJob(job3, "Payment gateway unavailable", 
            new RuntimeException("HTTP 503 Service Unavailable"));
        
        // Create some successful jobs
        String job4 = jobMonitoringService.startJob("DOCUMENT_VALIDATION", "Validating documents");
        jobMonitoringService.completeJob(job4, "Documents validated successfully");
        
        String job5 = jobMonitoringService.startJob("EMAIL_NOTIFICATION", "Sending notifications");
        jobMonitoringService.completeJob(job5, "Notifications sent successfully");
    }
}