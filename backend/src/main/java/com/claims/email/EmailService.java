package com.claims.email;

import com.claims.entity.Claim;
import com.claims.entity.FraudFlag;
import com.claims.entity.Investigator;
import com.claims.repository.InvestigatorRepository;
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
    
    public EmailService(JavaMailSender mailSender, InvestigatorRepository investigatorRepository) {
        this.mailSender = mailSender;
        this.investigatorRepository = investigatorRepository;
    }
    
    public void sendTestEmail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("srivanivadthya@gmail.com");
            message.setSubject("Test Email - Claims System");
            message.setText("This is a test email to verify email configuration.");
            message.setFrom("srivanivadthya@gmail.com");
            
            mailSender.send(message);
            log.info("Test email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send test email", e);
        }
    }
    
    @Async
    public void sendFraudAlert(Claim claim, List<FraudFlag> fraudFlags) {
        try {
            List<Investigator> investigators = investigatorRepository.findByActiveTrue();
            log.info("Found {} active investigators", investigators.size());
            
            String subject = "HIGH RISK FRAUD ALERT - Claim " + claim.getClaimReferenceNumber();
            String body = buildFraudAlertBody(claim, fraudFlags);
            
            for (Investigator investigator : investigators) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(investigator.getEmail());
                message.setSubject(subject);
                message.setText(body);
                message.setFrom("srivanivadthya@gmail.com");
                
                log.info("Attempting to send email to: {}", investigator.getEmail());
                mailSender.send(message);
                log.info("Fraud alert sent successfully to: {}", investigator.getEmail());
            }
        } catch (Exception e) {
            log.error("Failed to send fraud alert for claim: {}", claim.getClaimReferenceNumber(), e);
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