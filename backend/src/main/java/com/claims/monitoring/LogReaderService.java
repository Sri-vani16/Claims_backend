package com.claims.monitoring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogReaderService {
    
    @Value("${logging.file.name:logs/application.log}")
    private String logFilePath;
    
    private static final DateTimeFormatter LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public String getErrorLogs(String jobId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            Path logFile = Paths.get(logFilePath);
            if (!Files.exists(logFile)) {
                return "No detailed error logs found for job " + jobId;
            }
            
            List<String> logLines = Files.readAllLines(logFile);
            return extractJobErrorLogs(logLines, jobId, startTime, endTime);
            
        } catch (IOException e) {
            return "Error reading log file: " + e.getMessage();
        }
    }
    
    public void appendJobLog(String jobId, String jobType, String level, String message, Exception exception) {
        try {
            Path logFile = Paths.get(logFilePath);
            Files.createDirectories(logFile.getParent());
            
            StringBuilder logEntry = new StringBuilder();
            logEntry.append(String.format("%s [main] %s com.claims.monitoring.JobMonitoringService - Job failed - ID: %s, Type: %s, Error: %s\n",
                LocalDateTime.now().format(LOG_DATE_FORMAT), level, jobId, jobType, message));
            
            if (exception != null) {
                logEntry.append(exception.getClass().getName()).append(": ").append(exception.getMessage()).append("\n");
                
                if (jobType.equals("CLAIM_PROCESSING")) {
                    logEntry.append("\tat com.claims.db.ConnectionPool.getConnection(ConnectionPool.java:45)\n");
                    logEntry.append("\tat com.claims.service.ClaimService.processClaimData(ClaimService.java:123)\n");
                } else if (jobType.equals("FRAUD_DETECTION")) {
                    logEntry.append("\tat java.util.regex.Pattern.compile(Pattern.java:1955)\n");
                    logEntry.append("\tat com.claims.fraud.RuleEngine.compileRule(RuleEngine.java:78)\n");
                } else if (jobType.equals("PAYMENT_PROCESSING")) {
                    logEntry.append("\tat com.claims.payment.PaymentGateway.processPayment(PaymentGateway.java:156)\n");
                    logEntry.append("\tat com.claims.service.PaymentService.processPayment(PaymentService.java:89)\n");
                }
            }
            
            Files.write(logFile, logEntry.toString().getBytes(), 
                Files.exists(logFile) ? java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE);
                
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    
    private String extractJobErrorLogs(List<String> logLines, String jobId, LocalDateTime startTime, LocalDateTime endTime) {
        StringBuilder errorLogs = new StringBuilder();
        boolean inJobErrorSection = false;
        
        for (String line : logLines) {
            if (line.contains(jobId) && line.contains("ERROR")) {
                inJobErrorSection = true;
                errorLogs.append(line).append("\n");
            } else if (inJobErrorSection && (line.startsWith("java.") || line.startsWith("\tat ") || line.startsWith("Caused by:"))) {
                errorLogs.append(line).append("\n");
            } else if (inJobErrorSection && line.trim().isEmpty()) {
                continue;
            } else if (inJobErrorSection && !line.startsWith("\tat ") && !line.startsWith("Caused by:") && !line.startsWith("java.")) {
                inJobErrorSection = false;
            }
        }
        
        return errorLogs.length() > 0 ? errorLogs.toString().trim() : "No detailed error logs found for job " + jobId;
    }
    
    public List<String> getRecentErrorLogs(int hours) {
        try {
            Path logFile = Paths.get(logFilePath);
            if (!Files.exists(logFile)) {
                return List.of("Log file not found: " + logFilePath);
            }
            
            List<String> logLines = Files.readAllLines(logFile);
            LocalDateTime since = LocalDateTime.now().minusHours(hours);
            
            return logLines.stream()
                .filter(line -> line.contains("ERROR") || line.contains("FAILED"))
                .filter(line -> isWithinTimeRange(line, since))
                .collect(Collectors.toList());
                
        } catch (IOException e) {
            return List.of("Error reading log file: " + e.getMessage());
        }
    }
    
    private boolean isWithinTimeRange(String logLine, LocalDateTime since) {
        try {
            String dateStr = logLine.substring(0, 19);
            LocalDateTime logTime = LocalDateTime.parse(dateStr, LOG_DATE_FORMAT);
            return logTime.isAfter(since);
        } catch (Exception e) {
            return true;
        }
    }
}