package com.claims.email;

import com.claims.entity.Claim;
import com.claims.entity.FraudFlag;
import com.claims.entity.Investigator;
import com.claims.repository.InvestigatorRepository;
import com.claims.monitoring.JobMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {
    
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;
    private final InvestigatorRepository investigatorRepository;
    private final JobMonitoringService jobMonitoringService;
    
    public EmailService(JavaMailSender mailSender, InvestigatorRepository investigatorRepository,
                       JobMonitoringService jobMonitoringService) {
        this.mailSender = mailSender;
        this.investigatorRepository = investigatorRepository;
        this.jobMonitoringService = jobMonitoringService;
    }
    
    public void sendTestEmail() {
        String jobId = jobMonitoringService.startJob("EMAIL_TEST", "Sending test email");
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("srivanivadthya@gmail.com");
            message.setSubject("Test Email - Claims System");
            message.setText("This is a test email to verify email configuration.");
            message.setFrom("srivanivadthya@gmail.com");
            
            log.info("JobID: {} - Sending test email", jobId);
            mailSender.send(message);
            
            jobMonitoringService.completeJob(jobId, "Test email sent successfully");
            log.info("JobID: {} - Test email sent successfully", jobId);
        } catch (Exception e) {
            jobMonitoringService.failJob(jobId, "Failed to send test email", e);
            log.error("JobID: {} - Failed to send test email", jobId, e);
        }
    }
    
    @Async
    public void sendFraudAlert(Claim claim, List<FraudFlag> fraudFlags) {
        String jobId = jobMonitoringService.startJob("EMAIL_FRAUD_ALERT", 
            "Sending fraud alert for claim: " + claim.getClaimReferenceNumber());
        
        try {
            List<Investigator> investigators = investigatorRepository.findByActiveTrue();
            log.info("JobID: {} - Found {} active investigators for fraud alert", jobId, investigators.size());
            
            String subject = "HIGH RISK FRAUD ALERT - Claim " + claim.getClaimReferenceNumber();
            String body = buildFraudAlertBody(claim, fraudFlags);
            
            int emailsSent = 0;
            for (Investigator investigator : investigators) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(investigator.getEmail());
                message.setSubject(subject);
                message.setText(body);
                message.setFrom("srivanivadthya@gmail.com");
                
                log.info("JobID: {} - Attempting to send email to: {}", jobId, investigator.getEmail());
                mailSender.send(message);
                emailsSent++;
                log.info("JobID: {} - Fraud alert sent successfully to: {}", jobId, investigator.getEmail());
            }
            
            jobMonitoringService.completeJob(jobId, 
                "Fraud alert emails sent successfully to " + emailsSent + " investigators");
            
        } catch (Exception e) {
            jobMonitoringService.failJob(jobId, "Failed to send fraud alert emails", e);
            log.error("JobID: {} - Failed to send fraud alert for claim: {}", jobId, claim.getClaimReferenceNumber(), e);
        }
    }
    
    private String buildFraudAlertBody(Claim claim, List<FraudFlag> fraudFlags) {
        StringBuilder body = new StringBuilder();
        body.append("FRAUD ALERT - HIGH RISK CLAIM DETECTED\n\n");
        body.append("Claim Details:\n");
        body.append("- Claim Reference: ").append(claim.getClaimReferenceNumber()).append("\n");
        body.append("- Policy Number: ").append(claim.getPolicy().getPolicyNumber()).append("\n");
        body.append("- Claim Type: ").append(claim.getClaimType()).append("\n");
        body.append("- Incident Date: ").append(claim.getDateOfIncident()).append("\n");
        body.append("- Fraud Score: ").append(claim.getFraudScore()).append("\n\n");
        
        body.append("Triggered Fraud Rules:\n");
        for (FraudFlag flag : fraudFlags) {
            body.append("- ").append(flag.getRuleCode()).append(": ").append(flag.getRuleName())
                .append(" (Weight: ").append(flag.getWeight()).append(")\n");
            body.append("  Reason: ").append(flag.getReason()).append("\n\n");
        }
        
        body.append("Please investigate this claim immediately.\n\n");
        body.append("Claims Fraud Detection System");
        
        return body.toString();
    }
}